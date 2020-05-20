import java.io.Reader;
import java.io.IOException;
import java.util.List;

public class CatReader extends Reader {

    List<Reader> backing;
    int backing_idx;
    Reader backing_reader;

    public CatReader(List<Reader> backing) {
        this.backing = backing;
        this.backing_idx = -1;
        this.backing_reader = null;
    }

    Reader getCurrentReader() {
        if (this.backing_reader == null) {
            this.backing_idx++;
            try {
                this.backing_reader = this.backing.get(this.backing_idx);
            }
        }
        return this.backing_reader;
    }

    boolean isAtEnd() {
        return this.backing_reader == null && this.backing_idx >= this.backing.size();
    }

    void advanceReader() {
        this.backing_reader = null;
    }

    int checkRead(int result) {
        if (result == -1) {
            this.advanceReader();
            if (this.isAtEnd()) {
                return -1;
            } else {
                return 0;
            }
        } else {
            return result;
        }
    }

    @Override
    public void close() {
        for (Reader r : this.backing) {
            r.close();
        }
    }

    @Override
    public int read(CharBuffer target) throws IOException {
        if (this.isAtEnd()) return -1;
        return this.checkRead(this.getCurrentReader().read(target));
    }

    @Override
    public int read() throws IOException {
        if (this.isAtEnd()) return -1;
        return this.checkRead(this.currentReader().read());
    }

    @Override
    public int read(char[] cbuf) throws IOException {
        if (this.isAtEnd()) return -1;
        return this.checkRead(this.currentReader().read(cbuf));
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        if (this.isAtEnd()) return -1;
        return this.checkRead(this.currentReader().read(cbuf, off, len));
    }

    @Override
    public boolean ready() throws IOException {
        return (this.backing_reader == null) || this.backing_reader.ready();
    }
}
