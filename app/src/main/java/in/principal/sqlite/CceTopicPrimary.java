package in.principal.sqlite;

public class CceTopicPrimary {
    private int topicId;
    private int coScholasticId;
    private int sectionHeadingId;
    private String topicName;
    private int evaluation;

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public int getCoScholasticId() {
        return coScholasticId;
    }

    public void setCoScholasticId(int coScholasticId) {
        this.coScholasticId = coScholasticId;
    }

    public int getSectionHeadingId() {
        return sectionHeadingId;
    }

    public void setSectionHeadingId(int sectionHeadingId) {
        this.sectionHeadingId = sectionHeadingId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public int getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(int evaluation) {
        this.evaluation = evaluation;
    }
}
