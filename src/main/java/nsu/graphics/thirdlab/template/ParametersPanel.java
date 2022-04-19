package nsu.graphics.thirdlab.template;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Objects;
import java.util.Vector;

import static nsu.graphics.thirdlab.MathUtils.isNumeric;

public class ParametersPanel extends JPanel {
    private static final int minK = 4;
    private static final int maxK = 30;

    private static final int minN = 1;
    private static final int maxN = 30;

    private static final int minm = 2;
    private static final int maxm = 30;

    private static final int minM = 1;
    private static final int maxM = 30;
    private final ParametersListener listener;

    private final JPanel values;
    private final JTextField K;
    private final JTextField N;
    private final JTextField m;
    private final JTextField M;

    private final JComboBox<Integer> pointNumber;
    private final Vector<Integer> pointNumbersVector;
    private final JTextField x;
    private final JTextField y;


    private void initParameterLine(String text, JTextField field, ActionListener increase, ActionListener decrease) {
        JLabel label = new JLabel(text);

        JButton plus = new JButton();
        plus.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/add.png"))));
        plus.addActionListener(increase);

        JButton minus = new JButton();
        minus.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/remove.png"))));
        minus.addActionListener(decrease);
        values.add(label);
        values.add(field);
        values.add(plus);
        values.add(minus);
    }

    public void setValues(int newK, int newN, int newm, int newM) {
        K.setText(String.valueOf(newK));
        pointNumber.setSelectedIndex(0);
        setNumberOfPointsForPointChooser(newK);
        updateCoordinates();
        N.setText(String.valueOf(newN));
        m.setText(String.valueOf(newm));
        M.setText(String.valueOf(newM));
    }

    private void updateCoordinates() {
        Point p = listener.getPointPosition(pointNumber.getSelectedIndex());
        x.setText(String.valueOf(p.x));
        y.setText(String.valueOf(p.y));
    }

    private void setNumberOfPointsForPointChooser(int newNumberOfPoints) {
        int deltaK = newNumberOfPoints - pointNumbersVector.size();
        int lastNumber = pointNumbersVector.get(pointNumbersVector.size() - 1);
        for (int i = 1; i <= deltaK; i++) pointNumbersVector.add(lastNumber + i);
        if (0 > deltaK) {
            if (pointNumber.getSelectedIndex() == pointNumbersVector.size() - 1)
                pointNumber.setSelectedIndex(pointNumbersVector.size() - 2);
            pointNumbersVector.subList(pointNumbersVector.size() + deltaK, pointNumbersVector.size()).clear();
        }
        updateCoordinates();
    }

    public ParametersPanel(ParametersListener listener, int numberOfSegmentsPerInterval) {
        this.listener = listener;
        pointNumbersVector = new Vector<>();
        int initNumOfPoints = listener.getNumberOfPoints();
        for (int i = 0; i < initNumOfPoints; i++) pointNumbersVector.add(i);
        pointNumber = new JComboBox<>(pointNumbersVector);
        values = new JPanel(new GridLayout(4, 4));
        values.setBorder(BorderFactory.createTitledBorder("Parameters"));
        K = new JTextField(String.valueOf(initNumOfPoints), 2);
        K.addActionListener(e -> {
            if (checkValue(K.getText(), minK, maxK)) {
                listener.changePointNumber(Integer.parseInt(K.getText()));
                setNumberOfPointsForPointChooser(Integer.parseInt(K.getText()));
            }
        });
        initParameterLine("K", K, e -> {
            increaseValueInField(K, minK, maxK);
            listener.addPointToRight();
            setNumberOfPointsForPointChooser(Integer.parseInt(K.getText()));
        }, e -> {
            if (Integer.parseInt(K.getText()) > 4) {
                decreaseValueInField(K, minK, maxK);
                listener.deleteRightPoint();
                setNumberOfPointsForPointChooser(Integer.parseInt(K.getText()));
            }
        });
        N = new JTextField(String.valueOf(numberOfSegmentsPerInterval), 2);
        N.addActionListener(e -> {
            if (checkValue(K.getText(), minK, maxK)) {
                listener.setNumberOfSegmentsPerInterval(Integer.parseInt(N.getText()));
            }
        });
        initParameterLine("N", N, e -> {
            increaseValueInField(N, minN, maxN);
            listener.setNumberOfSegmentsPerInterval(Integer.parseInt(N.getText()));
        }, e -> {
            decreaseValueInField(N, minN, maxN);
            listener.setNumberOfSegmentsPerInterval(Integer.parseInt(N.getText()));
        });
        m = new JTextField(String.valueOf(minm), 2);
        initParameterLine("m", m, e -> {
            increaseValueInField(m, minm, maxm);
        }, e -> {
            decreaseValueInField(m, minm, maxm);
        });
        M = new JTextField(String.valueOf(2), 2);
        initParameterLine("M", M, e -> {
            increaseValueInField(M, minM, maxM);
        }, e -> {
            decreaseValueInField(M, minM, maxM);
        });

        JPanel points = new JPanel(new GridLayout(3, 2));
        points.setBorder(BorderFactory.createTitledBorder("Points"));

        JLabel labelNum = new JLabel("Num");
        Point pos = listener.getPointPosition(0);
        x = new JTextField(String.valueOf(pos.x), 4);
        y = new JTextField(String.valueOf(pos.y), 4);

        pointNumber.addActionListener(e -> updateCoordinates());
        x.addActionListener(e -> {
            listener.changePointPosition(pointNumber.getSelectedIndex(), new Point(Integer.parseInt(x.getText()), Integer.parseInt(y.getText())));
        });
        y.addActionListener(e -> {
            listener.changePointPosition(pointNumber.getSelectedIndex(), new Point(Integer.parseInt(x.getText()), Integer.parseInt(y.getText())));
        });
        points.add(labelNum);
        points.add(pointNumber);


        JLabel labelX = new JLabel("x");
        points.add(labelX);
        points.add(x);
        JLabel labelY = new JLabel("y");
        points.add(labelY);
        points.add(y);

        JPanel buttons = new JPanel(new GridLayout(3, 1));

        JButton accept = new JButton("Accept");
        JButton cancel = new JButton("Cancel");
        JButton normalize = new JButton("Normalize");
        normalize.addActionListener(e -> listener.normalize());
        cancel.addActionListener(e -> listener.disposeTemplateWindow());
        accept.addActionListener(e -> listener.acceptTemplate(Integer.parseInt(N.getText()), Integer.parseInt(K.getText()), Integer.parseInt(m.getText()), Integer.parseInt(M.getText())));
        buttons.add(normalize);
        buttons.add(cancel);
        buttons.add(accept);


        JPanel changeNumberOfPointsButtons = new JPanel(new GridLayout(3, 2));
        changeNumberOfPointsButtons.setBorder(BorderFactory.createTitledBorder("Add/remove points"));
        JLabel left = new JLabel("Left side");
        JLabel right = new JLabel("Right side");

        JButton addRight = new JButton();
        addRight.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/add.png"))));
        addRight.addActionListener(e -> {
            increaseValueInField(K, minK, maxK);
            listener.addPointToRight();
            setNumberOfPointsForPointChooser(Integer.parseInt(K.getText()));
        });

        JButton addLeft = new JButton();
        addLeft.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/add.png"))));
        addLeft.addActionListener(e -> {
            increaseValueInField(K, minK, maxK);
            listener.addPointToLeft();
            setNumberOfPointsForPointChooser(Integer.parseInt(K.getText()));
        });

        JButton deleteRight = new JButton();
        deleteRight.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/delete.png"))));
        deleteRight.addActionListener(e -> {
            if (Integer.parseInt(K.getText()) > 4) {
                decreaseValueInField(K, minK, maxK);
                listener.deleteRightPoint();
                setNumberOfPointsForPointChooser(Integer.parseInt(K.getText()));
            }
        });

        JButton deleteLeft = new JButton();
        deleteLeft.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/delete.png"))));
        deleteLeft.addActionListener(e -> {
            if (Integer.parseInt(K.getText()) > 4) {
                decreaseValueInField(K, minK, maxK);
                listener.deleteLeftPoint();
                setNumberOfPointsForPointChooser(Integer.parseInt(K.getText()));
            }
        });

        changeNumberOfPointsButtons.add(left);
        changeNumberOfPointsButtons.add(right);
        changeNumberOfPointsButtons.add(addLeft);
        changeNumberOfPointsButtons.add(addRight);
        changeNumberOfPointsButtons.add(deleteLeft);
        changeNumberOfPointsButtons.add(deleteRight);

        add(values);
        add(points);
        add(changeNumberOfPointsButtons);
        add(buttons);
        listener.acceptTemplate(Integer.parseInt(N.getText()), Integer.parseInt(K.getText()), Integer.parseInt(m.getText()), Integer.parseInt(M.getText()));
    }

    private void decreaseValueInField(JTextField field, int min, int max) {
        if (checkValue(field.getText(), min, max)) {
            int newValue = Integer.parseInt(field.getText()) - 1;
            if (newValue >= min) field.setText(String.valueOf(newValue));
        }
    }

    private void increaseValueInField(JTextField field, int min, int max) {
        if (checkValue(field.getText(), min, max)) {
            int newValue = Integer.parseInt(field.getText()) + 1;
            if (newValue <= max) field.setText(String.valueOf(newValue));
        }
    }

    private boolean checkValue(String val, int min, int max) {
        if (isNumeric(val)) {
            int value = Integer.parseInt(val);
            if (value >= min && value <= max) {
                return true;
            }
        }
        showMessage(val);
        return false;
    }

    private void showMessage(String str) {
        JOptionPane.showMessageDialog(this, str + " is bad value", "Parameter error", JOptionPane.INFORMATION_MESSAGE);
    }

    public int getK() {
        return Integer.parseInt(K.getText());
    }

    public int getN() {
        return Integer.parseInt(N.getText());
    }

    public int getm() {
        return Integer.parseInt(m.getText());
    }

    public int getM() {
        return Integer.parseInt(M.getText());
    }
}
