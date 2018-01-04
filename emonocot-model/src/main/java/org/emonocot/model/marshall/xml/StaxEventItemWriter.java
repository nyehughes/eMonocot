/*
 * This is eMonocot, a global online biodiversity information resource.
 *
 * Copyright © 2011–2015 The Board of Trustees of the Royal Botanic Gardens, Kew and The University of Oxford
 *
 * eMonocot is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * eMonocot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * The complete text of the GNU Affero General Public License is in the source repository as the file
 * ‘COPYING’.  It is also available from <http://www.gnu.org/licenses/>.
 */
package org.emonocot.model.marshall.xml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Result;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.WriteFailedException;
import org.springframework.batch.item.file.ResourceAwareItemWriterItemStream;
import org.springframework.batch.item.util.ExecutionContextUserSupport;
import org.springframework.batch.item.util.FileUtils;
import org.springframework.batch.item.xml.StaxUtils;
import org.springframework.batch.item.xml.StaxWriterCallback;
import org.springframework.batch.item.xml.stax.NoStartEndDocumentStreamWriter;
import org.springframework.batch.support.transaction.TransactionAwareBufferedWriter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.XmlMappingException;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * An implementation of {@link ItemWriter} which uses StAX and
 * {@link Marshaller} for serializing object to XML.
 *
 * This item writer also provides restart, statistics and transaction features
 * by implementing corresponding interfaces.
 *
 * The implementation is *not* thread-safe.
 *
 * @author Peter Zozom
 * @author Robert Kasanicky
 * @param <T> the type of item to write
 *
 */
public class StaxEventItemWriter<T> extends ExecutionContextUserSupport
implements ResourceAwareItemWriterItemStream<T>, InitializingBean {

	/**
	 *
	 */
	private static final Logger log = LoggerFactory.getLogger(StaxEventItemWriter.class);

	/**
	 * default encoding.
	 */
	private static final String DEFAULT_ENCODING = "UTF-8";

	/**
	 * default xml version.
	 */
	private static final String DEFAULT_XML_VERSION = "1.0";

	/**
	 * default root tag name.
	 */
	private static final String DEFAULT_ROOT_TAG_NAME = "root";

	/**
	 * restart data property name.
	 */
	private static final String RESTART_DATA_NAME = "position";

	/**
	 * restart data property name.
	 */
	private static final String WRITE_STATISTICS_NAME = "record.count";

	/**
	 * file system resource.
	 */
	private Resource resource;

	/**
	 * xml marshaller.
	 */
	private Marshaller marshaller;

	/**
	 * encoding to be used while reading from the resource.
	 */
	private String encoding = DEFAULT_ENCODING;

	/**
	 * XML version.
	 */
	private String version = DEFAULT_XML_VERSION;

	/**
	 * name of the root tag.
	 */
	private String rootTagName = DEFAULT_ROOT_TAG_NAME;

	/**
	 * namespace prefix of the root tag.
	 */
	private String rootTagNamespacePrefix = "";

	// namespace of the root tag
	private String rootTagNamespace = "";

	// root element attributes
	private Map<String, String> rootElementAttributes = null;

	// TRUE means, that output file will be overwritten if exists - default is
	// TRUE
	private boolean overwriteOutput = true;

	// file channel
	private FileChannel channel;

	// wrapper for XML event writer that swallows StartDocument and EndDocument
	// events
	private XMLEventWriter eventWriter;

	// XML event writer
	private XMLEventWriter delegateEventWriter;

	// current count of processed records
	private long currentRecordCount = 0;

	private boolean saveState = true;

	private StaxWriterCallback headerCallback;

	private StaxWriterCallback footerCallback;

	private Writer bufferedWriter;

	private boolean transactional = true;

	public StaxEventItemWriter() {
		setName(ClassUtils.getShortName(StaxEventItemWriter.class));
	}

	/**
	 * Set output file.
	 *
	 * @param resource
	 *            the output file
	 */
	public void setResource(Resource resource) {
		this.resource = resource;
	}

	/**
	 * Set Object to XML marshaller.
	 *
	 * @param marshaller
	 *            the Object to XML marshaller
	 */
	public void setMarshaller(Marshaller marshaller) {
		this.marshaller = marshaller;
	}

	/**
	 * headerCallback is called before writing any items.
	 */
	public void setHeaderCallback(StaxWriterCallback headerCallback) {
		this.headerCallback = headerCallback;
	}

	/**
	 * footerCallback is called after writing all items but before closing the
	 * file
	 */
	public void setFooterCallback(StaxWriterCallback footerCallback) {
		this.footerCallback = footerCallback;
	}

	/**
	 * Flag to indicate that writes should be deferred to the end of a
	 * transaction if present. Defaults to true.
	 *
	 * @param transactional
	 *            the flag to set
	 */
	public void setTransactional(boolean transactional) {
		this.transactional = transactional;
	}

	/**
	 * Get used encoding.
	 *
	 * @return the encoding used
	 */
	public String getEncoding() {
		return encoding;
	}

	/**
	 * Set encoding to be used for output file.
	 *
	 * @param encoding
	 *            the encoding to be used
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * Get XML version.
	 *
	 * @return the XML version used
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Set XML version to be used for output XML.
	 *
	 * @param version
	 *            the XML version to be used
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * Get the tag name of the root element.
	 *
	 * @return the root element tag name
	 */
	public String getRootTagName() {
		return rootTagName;
	}

	/**
	 * Set the tag name of the root element. If not set, default name is used
	 * ("root"). Namespace URI and prefix can also be set optionally using the
	 * notation:
	 *
	 * <pre>
	 * {uri}prefix:root
	 * </pre>
	 *
	 * The prefix is optional (defaults to empty), but if it is specified then
	 * the uri must be provided. In addition you might want to declare other
	 * namespaces using the {@link #setRootElementAttributes(Map) root
	 * attributes}.
	 *
	 * @param rootTagName
	 *            the tag name to be used for the root element
	 */
	public void setRootTagName(String rootTagName) {
		this.rootTagName = rootTagName;
	}

	/**
	 * Get the namespace prefix of the root element. Empty by default.
	 *
	 * @return the rootTagNamespacePrefix
	 */
	public String getRootTagNamespacePrefix() {
		return rootTagNamespacePrefix;
	}

	/**
	 * Get the namespace of the root element.
	 *
	 * @return the rootTagNamespace
	 */
	public String getRootTagNamespace() {
		return rootTagNamespace;
	}

	/**
	 * Get attributes of the root element.
	 *
	 * @return attributes of the root element
	 */
	public Map<String, String> getRootElementAttributes() {
		return rootElementAttributes;
	}

	/**
	 * Set the root element attributes to be written. If any of the key names
	 * begin with "xmlns:" then they are treated as namespace declarations.
	 *
	 * @param rootElementAttributes
	 *            attributes of the root element
	 */
	public void setRootElementAttributes(
			Map<String, String> rootElementAttributes) {
		this.rootElementAttributes = rootElementAttributes;
	}

	/**
	 * Set "overwrite" flag for the output file. Flag is ignored when output
	 * file processing is restarted.
	 *
	 * @param overwriteOutput
	 */
	public void setOverwriteOutput(boolean overwriteOutput) {
		this.overwriteOutput = overwriteOutput;
	}

	public void setSaveState(boolean saveState) {
		this.saveState = saveState;
	}

	/**
	 * @throws Exception
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(marshaller);
		if (rootTagName.contains("{")) {
			rootTagNamespace = rootTagName.replaceAll("\\{(.*)\\}.*", "$1");
			rootTagName = rootTagName.replaceAll("\\{.*\\}(.*)", "$1");
			if (rootTagName.contains(":")) {
				rootTagNamespacePrefix = rootTagName
						.replaceAll("(.*):.*", "$1");
				rootTagName = rootTagName.replaceAll(".*:(.*)", "$1");
			}
		}
	}

	/**
	 * Open the output source
	 *
	 * @see org.springframework.batch.item.ItemStream#open(ExecutionContext)
	 */
	public void open(ExecutionContext executionContext) {

		Assert.notNull(resource, "The resource must be set");

		long startAtPosition = 0;
		boolean restarted = false;

		// if restart data is provided, restart from provided offset
		// otherwise start from beginning
		if (executionContext.containsKey(getKey(RESTART_DATA_NAME))) {
			startAtPosition = executionContext
					.getLong(getKey(RESTART_DATA_NAME));
			restarted = true;
		}

		open(startAtPosition, restarted);

		if (startAtPosition == 0) {
			try {
				if (headerCallback != null) {
					headerCallback.write(delegateEventWriter);
				}
			} catch (IOException e) {
				throw new ItemStreamException("Failed to write headerItems", e);
			}
		}

	}

	/**
	 * Helper method for opening output source at given file position
	 */
	private void open(long position, boolean restarted) {

		File file;
		FileOutputStream os = null;

		try {
			file = resource.getFile();
			FileUtils.setUpOutputFile(file, restarted, false, overwriteOutput);
			Assert.state(resource.exists(), "Output resource must exist");
			os = new FileOutputStream(file, true);
			channel = os.getChannel();
			setPosition(position);
		} catch (IOException ioe) {
			throw new DataAccessResourceFailureException(
					"Unable to write to file resource: [" + resource + "]", ioe);
		}

		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();

		if (outputFactory
				.isPropertySupported("com.ctc.wstx.automaticEndElements")) {
			// If the current XMLOutputFactory implementation is supplied by
			// Woodstox >= 3.2.9 we want to disable its
			// automatic end element feature (see:
			// http://jira.codehaus.org/browse/WSTX-165) per
			// http://jira.springframework.org/browse/BATCH-761).
			outputFactory.setProperty("com.ctc.wstx.automaticEndElements",
					Boolean.FALSE);
		}
		if (outputFactory
				.isPropertySupported("com.ctc.wstx.outputValidateStructure")) {
			// On restart we don't write the root element so we have to disable
			// structural validation (see:
			// http://jira.springframework.org/browse/BATCH-1681).
			outputFactory.setProperty("com.ctc.wstx.outputValidateStructure",
					Boolean.FALSE);
		}

		try {
			if (transactional) {
				bufferedWriter = new TransactionAwareBufferedWriter(
						new OutputStreamWriter(os, encoding), new Runnable() {
							public void run() {
								closeStream();
							}
						});
			} else {
				bufferedWriter = new BufferedWriter(new OutputStreamWriter(os,
						encoding));
			}
			delegateEventWriter = outputFactory
					.createXMLEventWriter(bufferedWriter);
			eventWriter = new NoStartEndDocumentStreamWriter(
					delegateEventWriter);
			if (!restarted) {
				startDocument(delegateEventWriter);
			}
		} catch (XMLStreamException xse) {
			throw new DataAccessResourceFailureException(
					"Unable to write to file resource: [" + resource + "]", xse);
		} catch (UnsupportedEncodingException e) {
			throw new DataAccessResourceFailureException(
					"Unable to write to file resource: [" + resource
					                                    + "] with encoding=[" + encoding + "]", e);
		}

	}

	/**
	 * Writes simple XML header containing:
	 * <ul>
	 * <li>xml declaration - defines encoding and XML version</li>
	 * <li>opening tag of the root element and its attributes</li>
	 * </ul>
	 * If this is not sufficient for you, simply override this method. Encoding,
	 * version and root tag name can be retrieved with corresponding getters.
	 *
	 * @param writer
	 *            XML event writer
	 * @throws XMLStreamException
	 */
	protected void startDocument(XMLEventWriter writer)
			throws XMLStreamException {

		XMLEventFactory factory = XMLEventFactory.newInstance();

		// write start document
		writer.add(factory.createStartDocument(getEncoding(), getVersion()));

		// write root tag
		writer.add(factory.createStartElement(getRootTagNamespacePrefix(),
				getRootTagNamespace(), getRootTagName()));
		if (StringUtils.hasText(getRootTagNamespace())) {
			if (StringUtils.hasText(getRootTagNamespacePrefix())) {
				writer.add(factory.createNamespace(getRootTagNamespacePrefix(),
						getRootTagNamespace()));
			} else {
				writer.add(factory.createNamespace(getRootTagNamespace()));
			}
		}

		// write root tag attributes
		if (!CollectionUtils.isEmpty(getRootElementAttributes())) {

			for (Map.Entry<String, String> entry : getRootElementAttributes()
					.entrySet()) {
				String key = entry.getKey();
				if (key.startsWith("xmlns")) {
					String prefix = "";
					if (key.contains(":")) {
						prefix = key.substring(key.indexOf(":") + 1);
					}
					writer.add(factory.createNamespace(prefix, entry.getValue()));
				} else {
					writer.add(factory.createAttribute(key, entry.getValue()));
				}
			}

		}

		/*
		 * This forces the flush to write the end of the root element and avoids
		 * an off-by-one error on restart.
		 */
		writer.add(factory.createIgnorableSpace(""));
		writer.flush();

	}

	/**
	 * Writes the EndDocument tag manually.
	 *
	 * @param writer
	 *            XML event writer
	 * @throws XMLStreamException
	 */
	protected void endDocument(XMLEventWriter writer) throws XMLStreamException {

		// writer.writeEndDocument(); <- this doesn't work after restart
		// we need to write end tag of the root element manually

		String nsPrefix = !StringUtils.hasText(getRootTagNamespacePrefix()) ? ""
				: getRootTagNamespacePrefix() + ":";
		try {
			bufferedWriter.write("</" + nsPrefix + getRootTagName() + ">");
		} catch (IOException ioe) {
			throw new DataAccessResourceFailureException(
					"Unable to close file resource: [" + resource + "]", ioe);
		}
	}

	/**
	 * Flush and close the output source.
	 *
	 * @see org.springframework.batch.item.ItemStream#close()
	 */
	public void close() {

		// harmless event to close the root tag if there were no items
		XMLEventFactory factory = XMLEventFactory.newInstance();
		try {
			delegateEventWriter.add(factory.createCharacters(""));
		} catch (XMLStreamException e) {
			log.error(e.getLocalizedMessage());
		}

		try {
			if (footerCallback != null) {
				footerCallback.write(delegateEventWriter);
			}
			delegateEventWriter.flush();
			if (!delegateEventWriter.getClass().getName().equals("com.ctc.wstx.evt.WstxEventWriter")) {
				endDocument(delegateEventWriter);
			}
		} catch (IOException e) {
			throw new ItemStreamException("Failed to write footer items", e);
		} catch (XMLStreamException e) {
			throw new ItemStreamException("Failed to write end document tag", e);
		} finally {

			try {
				eventWriter.close();
			} catch (XMLStreamException e) {
				log.error("Unable to close file resource: [" + resource + "] "
						+ e);
			} finally {
				try {
					bufferedWriter.close();
				} catch (IOException e) {
					log.error("Unable to close file resource: [" + resource
							+ "] " + e);
				} finally {
					if (!transactional) {
						closeStream();
					}
				}
			}
		}
	}

	private void closeStream() {
		try {
			channel.close();
		} catch (IOException ioe) {
			log.error("Unable to close file resource: [" + resource + "] "
					+ ioe);
		}
	}

	/**
	 * Write the value objects and flush them to the file.
	 *
	 * @param items
	 *            the value object
	 * @throws IOException
	 * @throws XmlMappingException
	 */
	public void write(List<? extends T> items) throws XmlMappingException,
	Exception {

		currentRecordCount += items.size();

		for (Object object : items) {
			Assert.state(marshaller.supports(object.getClass()),
					"Marshaller must support the class of the marshalled object");
			Result result = StaxUtils.getResult(eventWriter);
			marshaller.marshal(object, result);
		}
		try {
			eventWriter.flush();
		} catch (XMLStreamException e) {
			throw new WriteFailedException("Failed to flush the events", e);
		}

	}

	/**
	 * Get the restart data.
	 *
	 * @see org.springframework.batch.item.ItemStream#update(ExecutionContext)
	 */
	public void update(ExecutionContext executionContext) {

		if (saveState) {
			Assert.notNull(executionContext,
					"ExecutionContext must not be null");
			executionContext.putLong(getKey(RESTART_DATA_NAME), getPosition());
			executionContext.putLong(getKey(WRITE_STATISTICS_NAME),
					currentRecordCount);
		}
	}

	/*
	 * Get the actual position in file channel. This method flushes any buffered
	 * data before position is read.
	 *
	 * @return byte offset in file channel
	 */
	private long getPosition() {

		long position;

		try {
			eventWriter.flush();
			position = channel.position();
			if (bufferedWriter instanceof TransactionAwareBufferedWriter) {
				position += ((TransactionAwareBufferedWriter) bufferedWriter)
						.getBufferSize();
			}
		} catch (Exception e) {
			throw new DataAccessResourceFailureException(
					"Unable to write to file resource: [" + resource + "]", e);
		}

		return position;
	}

	/**
	 * Set the file channel position.
	 *
	 * @param newPosition
	 *            new file channel position
	 */
	private void setPosition(long newPosition) {

		try {
			channel.truncate(newPosition);
			channel.position(newPosition);
		} catch (IOException e) {
			throw new DataAccessResourceFailureException(
					"Unable to write to file resource: [" + resource + "]", e);
		}

	}

}

