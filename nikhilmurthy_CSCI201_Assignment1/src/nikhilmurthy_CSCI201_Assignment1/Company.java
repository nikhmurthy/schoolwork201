package nikhilmurthy_CSCI201_Assignment1;

public class Company {
	private String name;
	private String ticker;
	private String description;
	private String startDate;
	private String exchangeCode;
	
	// class methods generated via https://www.jsonschema2pojo.org/
	public Company(String name, String ticker, String description, String startDate, String exchangeCode) {
		this.name = name;
		this.ticker = ticker;
		this.description = description;
		this.startDate = startDate;
		this.exchangeCode = exchangeCode;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTicker() {
		return ticker;
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getExchangeCode() {
		return exchangeCode;
	}

	public void setEC(String exchangeCode) {
		this.exchangeCode = exchangeCode;
	}

}
