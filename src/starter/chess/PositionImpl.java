package chess;

import java.util.Objects;

public class PositionImpl implements ChessPosition {
    private int row;
    private int col;

    public PositionImpl(int row, int col) {
        this.row = row;
        this.col = col;
    }

    @Override
    public int getRow() {
        return this.row;
    }

    @Override
    public int getColumn() {
        return this.col;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PositionImpl position = (PositionImpl) o;
        return row == position.row && col == position.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    public String toString() {
        return "(" + this.row + ", " + this.col + ")";
    }

}
