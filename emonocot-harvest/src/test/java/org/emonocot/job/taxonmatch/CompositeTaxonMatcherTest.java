/**
 * 
 */
package org.emonocot.job.taxonmatch;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.emonocot.api.match.Match;
import org.emonocot.api.match.MatchStatus;
import org.emonocot.api.match.taxon.TaxonMatcher;
import org.emonocot.model.taxon.Taxon;
import org.gbif.ecat.model.ParsedName;
import org.gbif.ecat.parser.NameParser;
import org.gbif.ecat.parser.UnparsableException;
import org.junit.Before;
import org.junit.Test;

/**
 * @author jk00kg
 *
 */
public class CompositeTaxonMatcherTest {
	
	CompositeTaxonMatcher underTest;
	
	/**
	 * 
	 */
	TaxonMatcher mock1 = EasyMock.createMock(TaxonMatcher.class);
	/**
	 * 
	 */
	TaxonMatcher mock2 = EasyMock.createMock(TaxonMatcher.class);
	
	/**
	 * 
	 */
	private List<Match<Taxon>> singleExact;
	
	/**
	 * 
	 */
	private List<Match<Taxon>> partials;

	/**
	 * Tests that only one matcher is used when possible to minimise unnecessary processing
	 * 
	 * Test method for {@link org.emonocot.job.taxonmatch.CompositeTaxonMatcher#match(org.gbif.ecat.model.ParsedName)}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testPrimaryMatch() throws Exception {
		ParsedName<String> input = new NameParser().parse(singleExact.get(0).getInternal().getName());
		expect(mock1.match(input)).andReturn(singleExact);
		
		replay(mock1, mock2);
		List<Match<Taxon>> actuals = underTest.match(input);
		assertEquals("We should have the single EXACT_MATCH", singleExact, actuals);
	}

	/**
	 * Tests that more matchers are used when only partial matches are found
	 * 
	 * Test method for {@link org.emonocot.job.taxonmatch.CompositeTaxonMatcher#match(org.gbif.ecat.model.ParsedName)}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testSecondaryMatch() throws Exception {
		ParsedName<String> input = new NameParser().parse(singleExact.get(0).getInternal().getName());
		expect(mock1.match(input)).andReturn(partials);
		expect(mock2.match(input)).andReturn(singleExact);
				
		replay(mock1, mock2);
		List<Match<Taxon>> actuals = underTest.match(input);
		assertEquals("We should have the single EXACT_MATCH", singleExact, actuals);
	}
	
	
	/**
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception{
		singleExact = new ArrayList<Match<Taxon>>();
		Taxon t = new Taxon();
		t.setName("Test name Arch.");
		t.setIdentifier("exact1");
		Match<Taxon> m = new Match<Taxon>();
		m.setInternal(t);
		m.setStatus(MatchStatus.EXACT);
		singleExact.add(m);

		partials = new ArrayList<Match<Taxon>>();
		for (int i = 0; i < 2; i++) {
			t = new Taxon();
			t.setName("Test part Arch.");
			t.setIdentifier("partial" + i);
			m = new Match<Taxon>();
			m.setInternal(t);
			m.setStatus(MatchStatus.PARTIAL);
			partials.add(m);
		}
		underTest = new CompositeTaxonMatcher();
		underTest.setMatchers(new TaxonMatcher[] {mock1, mock2});
	}

}
