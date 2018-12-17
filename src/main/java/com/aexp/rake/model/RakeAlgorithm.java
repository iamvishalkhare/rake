package com.aexp.rake.model;

/*
 *    RakeAlgorithm.java
 *    Copyright (C) 2014 Angel Conde, neuw84 at gmail dot com
 *
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

import com.aexp.rake.model.AbstractAlgorithm;
import com.aexp.rake.model.Document;
import com.aexp.rake.model.Term;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.toList;

public class RakeAlgorithm extends AbstractAlgorithm {

	private transient Document doc = null;
	private List<String> stopWordList;
	private List<Pattern> regexList = null;
	private List<String> punctList;
	private int minNumberOfletters = 2;

	public RakeAlgorithm() {
		super(true, "RAKE");
		stopWordList = new ArrayList<String>();
		regexList = new ArrayList<Pattern>();
		punctList = new ArrayList<String>();
	}

	@Override
	public void init(Document pDoc) {
		setDoc(pDoc);
		doc = pDoc;
	}

	public void loadStopWordsList(String pLoc) {
		List<String> stops = new ArrayList<String>();
		List<String> words = null;
		try {
			words = Files.readAllLines(Paths.get(pLoc), StandardCharsets.UTF_8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (String string : words) {
			stops.add(string.trim());
		}
		stopWordList = stops;
	}

	public void addCustomRegex(Pattern pat) {
		regexList.add(pat);
	}

	private Pattern buildStopWordRegex(List<String> pStopWords) {
		StringBuilder sb = new StringBuilder();
		for (String string : pStopWords) {
			sb.append("\\b").append(string.trim()).append("\\b").append("|");
		}
		String pattern = sb.substring(0, sb.length() - 1);
		Pattern pat = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
		return pat;
	}

	public void loadPunctStopWord() {
		List<String> stops = new ArrayList<String>();
		List<String> words = new ArrayList<String>();
		words.add(".");
		words.add(",");
		words.add("/");
		words.add("\"");
		words.add(">");
		words.add("<");
		words.add("[");
		words.add("]");
		words.add(";");
		words.add(":");
		words.add("#");
		words.add("-");
		words.add("_");
		words.add("(");
		words.add(")");
		words.add("{");
		words.add("}");
		words.add("'");
		words.add("*");
		words.add("=");
		words.add("&");
		// List<String> words = Files.readAllLines(Paths.get(pLoc), StandardCharsets.UTF_8);
		for (String string : words) {
			stops.add(string.trim());
		}
		punctList = stops;
	}

	public void loadPunctStopWord(List<String> pPunt) {
		punctList = pPunt;
	}

	private Pattern buildPunctStopWord(List<String> pPunctStop) {
		StringBuilder sb = new StringBuilder();
		for (String string : pPunctStop) {
			sb.append("\\").append(string.trim()).append("|");
		}
		String pattern = sb.substring(0, sb.length() - 1);
		Pattern pat = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
		return pat;
	}

	private List<String> generateCandidateKeywords(List<String> pSentenceList, List<Pattern> pStopWordPattern) {
		List<String> candidates = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		for (String string : pSentenceList) {
			for (Pattern pat : pStopWordPattern) {
				Matcher matcher = pat.matcher(string.trim());
				while (matcher.find()) {
					matcher.appendReplacement(sb, "|");
				}
				matcher.appendTail(sb);
				if (sb.length() > 0) {

					string = sb.toString();
				}
				sb = new StringBuffer();
			}
			List<String> cands = Arrays.asList(string.split("\\|"));
			for (String string1 : cands) {
				if (string1.trim().length() > 0) {
					String[] p = string1.trim().split("\\s+");
					if (string1.length() > 2 && p.length > 1 && !containsDigit(string1)) {
						candidates.add(string1.trim());
					}
				}
			}
		}
		return candidates;
	}

	@Override
	public void runAlgorithm() {
		if (stopWordList.isEmpty()) {
			System.out.println("The method " + this.getName() + " requires a StopWordList to build the candidate list");
		} else {
			Map<String, Integer> wordfreq = new HashMap<String, Integer>();
			Map<String, Integer> worddegree = new HashMap<String, Integer>();
			Map<String, Float> wordscore = new HashMap<String, Float>();
			Pattern pat = buildStopWordRegex(stopWordList);
			regexList.add(pat);
			if (!punctList.isEmpty()) {
				Pattern pat2 = buildPunctStopWord(punctList);
				regexList.add(pat2);
			}
			List<String> candidates = generateCandidateKeywords(doc.getSentenceList(), regexList);
			for (String phrase : candidates) {
				String[] wordlist = phrase.split("\\s+");
				int wordlistlength = wordlist.length;
				int wordlistdegree = wordlistlength - 1;
				for (String word : wordlist) {
					int freq;
					if (wordfreq.containsKey(word) == false) {
						wordfreq.put(word, 1);
					} else {
						freq = wordfreq.get(word) + 1;
						wordfreq.remove(word);
						wordfreq.put(word, freq);
					}

					if (worddegree.containsKey(word) == false) {
						worddegree.put(word, wordlistdegree);
					} else {
						int deg = worddegree.get(word) + wordlistdegree;
						worddegree.remove(word);
						worddegree.put(word, deg);
					}
				}
			}
			for (Map.Entry<String, Integer> entry : worddegree.entrySet()) {
				entry.setValue(entry.getValue() + wordfreq.get(entry.getKey()));
			}
			List<Term> termLi = new ArrayList<Term>();
			for (Map.Entry<String, Integer> entry : wordfreq.entrySet()) {
				wordscore.put(entry.getKey(), worddegree.get(entry.getKey()) / (wordfreq.get(entry.getKey()) * 1.0f));
			}
			for (String phrase : candidates) {
				String[] words = phrase.split("\\s+");
				float score = 0.0f;
				for (String word : words) {
					score += wordscore.get(word);
				}
				termLi.add(new Term(phrase, score));
			}
			Comparator<? super Term> sorter = (o1, o2) -> o1.getScore() > o2.getScore() ? -1
					: o1.getScore() == o2.getScore() ? 0 : 1;
			List<Term> orderedList = termLi.parallelStream().sorted(sorter).distinct().collect(toList());
			doc.setTermList(orderedList);

		}
	}

	/**
	 *
	 * @return the doc
	 */
	public Document getDoc() {
		return doc;
	}

	/**
	 * @param doc
	 *            the doc to set
	 */
	public void setDoc(Document doc) {
		this.doc = doc;
	}

	/**
	 *
	 * Returns the current (Default 2)
	 *
	 * @return the minNumberOfletters required to a word to be included
	 */
	public int getMinNumberOfletters() {
		return minNumberOfletters;
	}

	/**
	 * Default 2
	 *
	 * @param minNumberOfletters
	 *            the minNumberOfletters to set to a word to be included
	 */
	public void setMinNumberOfletters(int minNumberOfletters) {
		this.minNumberOfletters = minNumberOfletters;
	}

	private boolean containsDigit(String string) {
		for (char c : string.toCharArray()) {
			if (Character.isDigit(c)) {
				return true;
			}
		}
		return false;
	}

	public static void main(String[] args) throws IOException {
//		Document doc = new Document("/Users/vkhar11/Desktop/", "rakefile.txt");
//		doc.setSentenceList(sentences);
//		doc.setTokenList(tokenized_sentences);
		RakeAlgorithm ex = new RakeAlgorithm();
		// Path of a file containing all stopwords 
		ex.loadStopWordsList("/Users/vkhar11/workspaces/rake/src/main/resources/rakestopwords.txt");
		ex.loadPunctStopWord();
//		PlainTextDocumentReaderLBJEn parser = new PlainTextDocumentReaderLBJEn();
//		parser.readSource("testCorpus/textAstronomy");
		// Path of a file containing the actual text from which keywords are to be extracted. 
		Document doc = new Document("/Users/vkhar11/workspaces/rake/src/main/resources/", "rakefile.txt");
		ex.init(doc);
		ex.runAlgorithm();
		for(Term item: doc.getTermList()) {
			System.out.println(item.getTerm() + "---" + item.getScore());
		}
	}
}
