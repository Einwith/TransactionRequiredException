package com.lixar.apba.domain;

public interface IBookEntry {

    String DEFAULT_CONDITION = "default";

    int getId();

    void setId(int id);

    Integer getClient();

    void setClient(Integer client);

    int getPage();

    void setPage(int page);

    int getSection();

    void setSection(int section);

    String getResultId();

    void setResultId(String resultId);

    String getResultBook();

    void setResultBook(String resultBook);

    String getResultCurrent();

    void setResultCurrent(String resultCurrent);

    String getLocal();

    void setLocal(String local);

    String getTextCondition();

    void setTextCondition(String textCondition);

    String getParserCondition();
}
