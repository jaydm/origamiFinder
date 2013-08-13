package net.jnwd.origamiData;

public class Model implements Comparable<Model> {
	public static final String[] FIELDS = {
		"Name:",
		"Model Type:",
		"Creator:",
		"Paper:",
		"Book Title:",
		"ISBN:",
		"Glue:",
		"Page:",
		"Cuts:",
		"Pieces:",
		"Difficulty"
	};

	public static final String COL_MODEL_NAME = "MODELNAME";
	public static final String COL_MODEL_TYPE = "MODELTYPE";
	public static final String COL_CREATOR = "CREATOR";
	public static final String COL_BOOK_TITLE = "BOOKTITLE";
	public static final String COL_ISBN = "ISBN";
	public static final String COL_ON_PAGE = "ONPAGE";
	public static final String COL_DIFFICULTY = "DIFFICULTY";
	public static final String COL_PAPER = "PAPER";
	public static final String COL_PIECES = "PIECES";
	public static final String COL_GLUE = "GLUE";
	public static final String COL_CUTS = "CUTS";

	public static final String tableName = "model";

	public long id;
	public String name;
	public String modelType;
	public String creator;
	public String paper;
	public String bookTitle;
	public String ISBN;
	public String page;
	public String pieces;
	public String glue;
	public String cuts;
	public String difficulty;

	public Model() {
		super();

		id = 0L;

		name = "";
		modelType = "";
		creator = "";
		paper = "";
		bookTitle = "";
		ISBN = "";
		page = "";
		pieces = "";
		glue = "";
		cuts = "";
		difficulty = "Not Rated";
	}

	public Model(String allData) {
		this();

		String[] data = allData.split("~");

		id = Long.parseLong(data[0]);
		name = data[1];
		modelType = data[2];
		difficulty = data[3];
		creator = data[4];
		bookTitle = data[5];
		ISBN = data[6];
		page = data[7];
		pieces = data[8];
		glue = data[9];
		cuts = data[10];
	}

	@Override
	public boolean equals(Object that) {
		if (this == that) {
			return true;
		}

		if (that == null) {
			return false;
		}

		if (this.getClass() != that.getClass()) {
			return false;
		}

		Model test = (Model) that;

		if (id != test.id) {
			return false;
		}

		if (! name.equals(test.name)) {
			return false;
		}

		if (! ISBN.equals(test.ISBN)) {
			return false;
		}

		return true;
	}

	@Override
	public int compareTo(Model that) {
		if (this == that) {
			return 0;
		}

		String thisKey = name + "~" + ISBN;
		String thatKey = that.name + "~" + that.ISBN;

		if (this.id == that.id) {
			return thisKey.compareTo(thatKey);
		}

		return ((id - that.id) < 0 ? - 1 : 1);
	}

	@Override
	public String toString() {
		return "" +
			"id: " + id + "\n" +
			"name: " + name + "\n" +
			"model type: " + modelType + "\n" +
			"creator: " + creator + "\n" +
			"paper: " + paper + "\n" +
			"bookTitle: " + bookTitle + "\n" +
			"ISBN: " + ISBN + "\n" +
			"page: " + page + "\n" +
			"pieces: " + pieces + "\n" +
			"glue: " + glue + "\n" +
			"cuts: " + cuts + "\n" +
			"difficulty: " + difficulty + "\n";
	}

	public static String enQuote(String value) {
		return "\"" + value.replace('"', '\'') + "\"";
	}

	public String toSQL() {
		return "" +
			"insert into " + tableName + " values (" +
			id + "," +
			enQuote(name) + "," +
			enQuote(modelType) + "," +
			enQuote(difficulty) + "," +
			enQuote(creator) + "," +
			enQuote(bookTitle) + "," +
			enQuote(ISBN) + "," +
			page + "," +
			pieces + "," +
			(glue.equals("no") ? false : true) + "," +
			(cuts.equals("no") ? false : true) + ");";
	}

	public String toJava() {
		return "" +
			"allModels.add(new Model(" +
			id + "," +
			enQuote(name) + "," +
			enQuote(modelType) + "," +
			enQuote(difficulty) + "," +
			enQuote(creator) + "," +
			enQuote(bookTitle) + "," +
			enQuote(ISBN) + "," +
			page + "," +
			pieces + "," +
			(glue.equals("no") ? false : true) + "," +
			(cuts.equals("no") ? false : true) + "));\n";
	}

	public String toRaw() {
		return "" +
			id + "~" +
			enQuote(name) + "~" +
			enQuote(modelType) + "~" +
			enQuote(difficulty) + "~" +
			enQuote(creator) + "~" +
			enQuote(bookTitle) + "~" +
			enQuote(ISBN) + "~" +
			page + "~" +
			pieces + "~" +
			(glue.equals("no") ? false : true) + "~" +
			(cuts.equals("no") ? false : true);
	}

	private String getData(String data) {
		String work = data.substring(data.indexOf('>') + 1);

		return work.substring(0, work.indexOf('<')).trim();
	}

	public static boolean hasData(String row) {
		for (String test : FIELDS) {
			if (row.startsWith(test)) {
				return true;
			}
		}

		return false;
	}

	public void grab(String row) {
		if (row.startsWith("Name:")) {
			name = getData(row);

			return;
		}

		if (row.startsWith("Model Type:")) {
			modelType = getData(row);

			return;
		}

		if (row.startsWith("Creator:")) {
			creator = getData(row);

			return;
		}

		if (row.startsWith("Paper:")) {
			paper = getData(row);

			return;
		}

		if (row.startsWith("Book Title:")) {
			bookTitle = getData(row);

			return;
		}

		if (row.startsWith("ISBN:")) {
			ISBN = getData(row);

			return;
		}

		if (row.startsWith("Glue:")) {
			glue = getData(row);

			return;
		}

		if (row.startsWith("Page:")) {
			page = getData(row);

			return;
		}

		if (row.startsWith("Cuts:")) {
			cuts = getData(row);

			return;
		}

		if (row.startsWith("Pieces:")) {
			pieces = getData(row);

			return;
		}

		if (row.startsWith("Difficulty")) {
			if (row.indexOf("alt=") >= 0) {
				String temp = row.substring(row.indexOf("alt=") + 5);

				temp = temp.substring(0, temp.indexOf('"'));

				difficulty = temp;
			}

			{
				String temp = row.substring(row.indexOf("ModelID=") + 8);
				temp = temp.substring(0, temp.indexOf('"'));

				id = Long.parseLong(temp);
			}

			return;
		}
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getModelType() {
		return modelType;
	}

	public void setModelType(String modelType) {
		this.modelType = modelType;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getPaper() {
		return paper;
	}

	public void setPaper(String paper) {
		this.paper = paper;
	}

	public String getBookTitle() {
		return bookTitle;
	}

	public void setBookTitle(String bookTitle) {
		this.bookTitle = bookTitle;
	}

	public String getISBN() {
		return ISBN;
	}

	public void setISBN(String iSBN) {
		ISBN = iSBN;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public String getPieces() {
		return pieces;
	}

	public void setPieces(String pieces) {
		this.pieces = pieces;
	}

	public String getGlue() {
		return glue;
	}

	public void setGlue(String glue) {
		this.glue = glue;
	}

	public String getCuts() {
		return cuts;
	}

	public void setCuts(String cuts) {
		this.cuts = cuts;
	}

	public String getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(String difficulty) {
		this.difficulty = difficulty;
	}
}
