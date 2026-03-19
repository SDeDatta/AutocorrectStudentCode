public class Pair
{
    String word;
    int editDistance;
    public Pair(String word, int editDistance)
    {
        this.word = word;
        this.editDistance = editDistance;
    }

    public int getEditDistance() {
        return editDistance;
    }
    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setEditDistance(int editDistance) {
        this.editDistance = editDistance;
    }
}
