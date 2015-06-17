package utils.wordmodel;

public class DataGenerationParameterUtils {

	public final static String[] outprefix = {"data/omp/"};
	
	public final static String[] relations = {"PersonParty","ProductRecall","CompanyLaborIssues","VotingResult","CompanyLegalIssues",
		"PersonLocation","IPO","CompanyMeeting","NaturalDisaster","CandidatePosition","Quotation","CompanyAffiliates",
		"DiplomaticRelations","ContactDetails","AnalystRecommendation","Buybacks","PatentFiling","CompanyInvestment",
		"CompanyLayoffs","Conviction","Indictment","EmploymentChange","ConferenceCall","Bankruptcy","StockSplit",
		"Dividend","CompanyCompetitor","CompanyEmployeesNumber","Trial","CompanyNameChange","DelayedFiling",
		"PoliticalEndorsement","CreditRating","BusinessRelation","BonusSharesIssuance","Acquisition",
		"CompanyForceMajeure","CompanyProduct","PersonCommunication","ArmedAttack","CompanyUsingProduct",
		"IndicesChanges","CompanyEarningsAnnouncement","MusicAlbumRelease","CompanyTechnology","PersonAttributes",
		"CompanyExpansion","CompanyFounded","AnalystEarningsEstimate","PersonEducation","PatentIssuance",
		"JointVenture","Arrest","MovieRelease","PersonEmailAddress","FDAPhase","SecondaryIssuance","GenericRelations",
		"CompanyRestatement","EquityFinancing","ManMadeDisaster","ArmsPurchaseSale","MilitaryAction","ProductIssues",
		"Alliance","DebtFinancing","CompanyLocation","PoliticalRelationship","EnvironmentalIssue","PersonTravel",
		"Extinction","CompanyTicker","CompanyReorganization","CompanyAccountingChange","PersonCareer","Merger",
		"EmploymentRelation","ProductRelease","CompanyListingChange","PersonRelation","CompanyEarningsGuidance",
		"FamilyRelation","PollsResult","CompanyCustomer"};
	
	public final static String[] extractors = {"default"};
	
	public final static String[] tasks = {"Train", "Validation", "Test"};
	
	public final static String[] word2Vecs = {"","",""};

	public static boolean isBinary(String word2vec) {
		if (word2vec.equals("C:/Users/Pablo/Downloads/GoogleNews-vectors-negative300.bin"))
			return true;
		throw new UnsupportedOperationException("Model does not exist");
	}

	public static int getLongestNGram(String word2vec) {
		if (word2vec.equals("C:/Users/Pablo/Downloads/GoogleNews-vectors-negative300.bin"))
			return 2;
		throw new UnsupportedOperationException("Model does not exist");
	}

	public static String getSourceFolder(String task) {
		if (task.equals("Toy")){
			return "D:/Documents/NYTimesExtraction/NYTToyExtraction/";
		}
		return "D:/Documents/NYTimesExtraction/NYTTrainExtraction/NYT"+task+"Extraction/";
	}

	public static String getFileListName(String task) {
		return task + ".filelist";
	}

	public static String getAttributesFileName(String relation,
			String extractor, String task) {
		return task + "." + relation + "." + extractor + ".ser";
	}

	
}
