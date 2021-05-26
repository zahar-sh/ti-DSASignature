package sample;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.IntSupplier;

public class Controller {
    private static boolean isNotPrime(BigInteger q) {
        return !(q.testBit(0) && q.isProbablePrime(100));
    }
    public static BigInteger exp(BigInteger a1, BigInteger z1, BigInteger n) {
        BigInteger x = BigInteger.ONE;
        while (z1.signum() != 0) {
            while (z1.mod(BigInteger.TWO).signum() == 0) {
                z1 = z1.shiftRight(1);
                a1 = a1.multiply(a1).mod(n);
            }
            z1 = z1.subtract(BigInteger.ONE);
            x = x.multiply(a1).mod(n);
        }
        return x;
    }
    public static BigInteger[] advancedEuclideanAlgorithm(BigInteger a, BigInteger b) {
        BigInteger d0 = a;
        BigInteger d1 = b;
        BigInteger x0 = BigInteger.ONE;
        BigInteger x1 = BigInteger.ZERO;
        BigInteger y0 = BigInteger.ZERO;
        BigInteger y1 = BigInteger.ONE;


        while (d1.compareTo(BigInteger.ONE) > 0) { //  IF d1 > 1
            BigInteger q = d0.divide(d1); // q = d0/d1
            BigInteger d2 = d0.mod(d1); // d2 = d0 % d1;
            BigInteger x2 = x0.subtract(q.multiply(x1)); //x2 = x0 - q*x1;
            BigInteger y2 = y0.subtract(q.multiply(y1));  // y2 = y0 - q*y1;
            d0 = d1;
            d1 = d2;
            x0 = x1;
            x1 = x2;
            y0 = y1;
            y1 = y2;
        }
        return new BigInteger[]{x1, y1, d1};
    }
    public static BigInteger modInverse(BigInteger a, BigInteger b) {
        BigInteger[] coefs = advancedEuclideanAlgorithm(b, a);
        BigInteger y1 = coefs[1];
        while (y1.compareTo(BigInteger.ZERO) < 0)
            y1 = y1.add(b);
        return y1;
    }
    public static BigInteger hash(IntSupplier s, BigInteger n) {
        BigInteger hash = BigInteger.valueOf(100);
        long next;
        while ((next = s.getAsInt()) != -1) {
            hash = hash.add(BigInteger.valueOf(next));
            hash = hash.multiply(hash).mod(n);
        }
        return hash;
    }
    public static long readLastLines(File file, int count, Consumer<String> out) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            StringBuilder sb = new StringBuilder();
            int lines = 0;
            long seek = file.length() - 1;
            while (seek >= 0) {
                raf.seek(seek);
                char c = (char) raf.read();
                if (c == '\n') {
                    out.accept(sb.reverse().toString());
                    sb.setLength(0);
                    if (++lines == count)
                        return seek + 1;
                } else {
                    sb.append(c);
                }
                seek--;
            }
            out.accept(sb.reverse().toString());
            return 0;
        }
    }

    private static final int N = 1024;
    private static final int L = 160;

    @FXML
    private CheckBox autoq, autop, autoh, autox, autok;
    @FXML
    private TextField qTF, pTF, hTF, gTF, xTF, yTF, kTF, rTF, sTF, signPath, hashTF;
    private BigInteger q, p, h, g, x, y, k;

    @FXML
    private TextField rTF0, sTF0, wTF, u1TF, u2TF, vTF, checkPath, hashTF0;
    @FXML
    private Label out;

    private final Random random = ThreadLocalRandom.current();
    private final Map<TextField, BigInteger> textIntCash = new HashMap<>();
    private boolean canInvalidate = false;

    private BigInteger get(TextField key) {
        return textIntCash.computeIfAbsent(key, field -> {
            try {
                return new BigInteger(field.getText());
            } catch (Exception e) {
                return null;
            }
        });
    }

    private void info(String msg) {
        out.setTextFill(Color.BLACK);
        out.setText(msg);
    }
    private void err(String err) {
        out.setTextFill(Color.RED);
        out.setText(err);
    }

    private BigInteger calcHash(String s, long l) throws IOException {
        try (InputStream in = new BufferedInputStream(
                new FileInputStream(s))) {
            return hash(new IntSupplier() {
                int count = 0;

                public int getAsInt() {
                    try {
                        if (count >= l)
                            return -1;
                        count++;
                        return in.read();
                    } catch (IOException e) {
                        err("Calc hash error");
                        return -1;
                    }
                }
            }, q);
        }
    }

    private void signImpl() {
        if (autoq.isSelected()) {
            q = BigInteger.probablePrime(L, random);
            qTF.setText(q.toString());
            textIntCash.put(qTF, q);
        } else {
            if (q == null) {
                q = get(qTF);
                if (q == null) {
                    err("Illegal characters q");
                    return;
                }
                /*if (q.bitLength() < L) {
                    err("q bit length is small, need " + L);
                    q = null;
                    return;
                }*/
                if (isNotPrime(q)) {
                    err("q is not Prime");
                    q = null;
                    return;
                }
            }
        }
        BigInteger t;
        if (autop.isSelected()) {
            BigInteger a;
            do {
                a = new BigInteger(random.nextInt() % (N >> 1) + N, random);
                if (a.testBit(0))
                    a = a.clearBit(0);
                t = a.multiply(q);
                p = t.add(BigInteger.ONE);
            } while (isNotPrime(p));
            pTF.setText(p.toString());
            textIntCash.put(pTF, p);
        } else {
            if (p == null) {
                p = get(pTF);
                if (p == null) {
                    err("Illegal characters p");
                    return;
                }
                /*if (p.bitLength() < N) {
                    err("p bit length is small, need " + N);
                    p = null;
                    return;
                }*/
                if (isNotPrime(p)) {
                    err("p is not Prime");
                    p = null;
                    return;
                }
                t = p.subtract(BigInteger.ONE);
                if (t.mod(q).signum() != 0) {
                    err("p - 1 mod q != 0");
                    p = null;
                    q = null;
                    return;
                }
            } else {
                t = p.subtract(BigInteger.ONE);
            }
        }

        if (autoh.isSelected()) {
            h = BigInteger.TWO;
            BigInteger e = t.divide(q);
            while ((g = exp(h, e, p)).compareTo(BigInteger.TWO) < 0)
                h = h.add(BigInteger.ONE);
            hTF.setText(h.toString());
            textIntCash.put(hTF, h);
        } else {
            if (h == null) {
                h = get(hTF);
                if (h == null) {
                    err("Illegal characters h");
                    return;
                }
                if (!(BigInteger.ONE.compareTo(h) < 0 && h.compareTo(t) < 0)) {
                    err("Expected: 1 < h < p - 1");
                    h = null;
                    return;
                }
            }
            g = exp(h, t.divide(q), p);
            if (g.compareTo(BigInteger.TWO) < 0) {
                err("g == 1");
                h = null;
                return;
            }
        }
        gTF.setText(g.toString());

        if (autox.isSelected()) {
            x = new BigInteger(q.bitLength() - 2, random).add(BigInteger.ONE);
            xTF.setText(x.toString());
            textIntCash.put(xTF, x);
        } else {
            if (x == null) {
                x = get(xTF);
                if (x == null) {
                    err("Illegal characters x");
                    return;
                }
                if (!(BigInteger.ZERO.compareTo(x) < 0 && x.compareTo(q) < 0)) {
                    err("Expected: 0 < x < q");
                    x = null;
                    return;
                }
            }
        }

        y = exp(g, x, p);
        yTF.setText(y.toString());

        String path = signPath.getText();
        BigInteger hash;
        try {
            hash = calcHash(path, Long.MAX_VALUE);
            hashTF.setText(hash.toString());
        } catch (FileNotFoundException e) {
            err("File not found");
            return;
        } catch (IOException e) {
            err("Input output exception");
            return;
        }

        BigInteger r, s;
        if (autok.isSelected()) {
            while (true) {
                k = new BigInteger(q.bitLength() - 2, random)
                        .add(BigInteger.ONE);
                r = exp(g, k, p).mod(q);
                if (r.signum() == 0)
                    continue;
                s = modInverse(k, q).multiply(hash.add(x.multiply(r)).mod(q)).mod(q);
                if (s.signum() == 0)
                    continue;
                break;
            }
            kTF.setText(k.toString());
            textIntCash.put(kTF, k);
        } else {
            if (k == null) {
                k = get(kTF);
                if (k == null) {
                    err("Illegal characters k");
                    return;
                }
                if (!(BigInteger.ZERO.compareTo(k) < 0 && k.compareTo(q) < 0)) {
                    err("Expected: 0 < k < q");
                    k = null;
                    return;
                }
            }
            r = exp(g, k, p).mod(q);
            if (r.signum() == 0) {
                err("r == 0");
                k = null;
                return;
            }
            s = modInverse(k, q).multiply(hash.add(x.multiply(r)).mod(q)).mod(q);
            if (s.signum() == 0) {
                err("r == 0");
                k = null;
                return;
            }
        }

        String rStr = r.toString();
        String sStr = s.toString();
        rTF.setText(rStr);
        sTF.setText(sStr);

        try {
            Files.writeString(Path.of(path),
                    String.format("%s%s\n%s", (new File(path).length() == 0 ? "" : "\n"), rStr, sStr),
                    StandardOpenOption.APPEND);
        } catch (IOException e) {
            err("IOException");
        }
        info("Signed");
    }
    private void checkImpl() {
        BigInteger r0, s0, hash0;
        rTF0.setText("");
        sTF0.setText("");
        wTF.setText("");
        u1TF.setText("");
        u2TF.setText("");
        vTF.setText("");
        hashTF0.setText("");
        try {
            String path = checkPath.getText();
            ArrayList<String> list = new ArrayList<>(2);
            long l = readLastLines(new File(path), 2, list::add);
            if (list.size() != 2) {
                info("FALSE");
                return;
            }
            r0 = new BigInteger(list.get(1));
            s0 = new BigInteger(list.get(0));

            rTF0.setText(list.get(1));
            sTF0.setText(list.get(0));

            hash0 = calcHash(path, l - 1);
            hashTF0.setText(hash0.toString());
        } catch (FileNotFoundException e) {
            err("File not found");
            return;
        } catch (IOException e) {
            err("Input output exception");
            return;
        } catch (NumberFormatException e) {
            info("FALSE");
            return;
        }
        try {
            BigInteger w = modInverse(s0, q);
            BigInteger u1 = hash0.multiply(w).mod(q);
            BigInteger u2 = r0.multiply(w).mod(q);
            BigInteger v = exp(g, u1, p).multiply(exp(y, u2, p)).mod(p).mod(q);

            wTF.setText(w.toString());
            u1TF.setText(u1.toString());
            u2TF.setText(u2.toString());
            vTF.setText(v.toString());

            if (v.compareTo(r0) != 0) {
                info("FALSE");
                return;
            }
            info("TRUE");
        } catch (NullPointerException e) {
            err("Signature not defined");
        }
    }

    @FXML
    private void initialize() {
        out.setOnMouseReleased(e -> out.setText(""));

        for (TextField field : new TextField[]{qTF, pTF, hTF, xTF, kTF}) {
            field.textProperty().addListener(observable -> {
                if (canInvalidate) {
                    p = q = h = g = x = y = k = null;
                    gTF.setText("");
                    yTF.setText("");
                    rTF.setText("");
                    sTF.setText("");
                    textIntCash.remove(field);
                }
            });
        }

        FileChooser chooser = new FileChooser();
        EventHandler<MouseEvent> pathClick = event -> {
            if (event.getClickCount() > 1) {
                File f = chooser.showOpenDialog(null);
                if (f != null) {
                    ((TextInputControl) event.getSource()).
                            setText(f.getAbsolutePath());
                }
            }
        };
        signPath.setOnMouseClicked(pathClick);
        checkPath.setOnMouseClicked(pathClick);

        signPath.textProperty().addListener(observable -> hashTF.setText(""));
        checkPath.textProperty().addListener(observable -> hashTF0.setText(""));
    }

    @FXML
    private void sign() {
        try {
            canInvalidate = false;
            signImpl();
        } finally {
            canInvalidate = true;
        }
    }
    @FXML
    private void check() {
        try {
            canInvalidate = false;
            checkImpl();
        } finally {
            canInvalidate = true;
        }
    }
}