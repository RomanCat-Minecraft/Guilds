package uk.firedev.guilds.utils;

public class Pair<L, R> {

    private L left;
    private R right;

    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public static <L, R> Pair<L, R> empty() {
        return new Pair<>(null, null);
    }

    public void setLeft(L left) {
        this.left = left;
    }

    public L getLeft() {
        return left;
    }

    public void setRight(R right) {
        this.right = right;
    }

    public R getRight() {
        return right;
    }

}
