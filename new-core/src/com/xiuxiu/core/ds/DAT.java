package com.xiuxiu.core.ds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DAT {
    private static class Node {
        protected int code;
        protected int depth;
        protected int left;
        protected int right;
    }

    private int[] base;
    private int[] check;

    private boolean[] used;
    private List<String> keys;
    private int keySize;

    private int size;
    private int allocSize;
    private int process;
    private int nextCheckPosition;
    private int error;

    public DAT() {
        this.base = null;
        this.check = null;
        this.used = null;
        this.size = 0;
        this.allocSize = 0;
        this.error = 0;
    }

    public void build(List<String> keys) {
        if (null == keys) {
            return;
        }
        this.keys = keys;
        this.keySize = keys.size();
        this.process = 0;

        this.resize(65536 * 32);

        this.base[0] = 1;
        this.nextCheckPosition = 0;

        Node root = new Node();
        root.left = 0;
        root.right = this.keySize;
        root.depth = 0;

        List<Node> siblings = new ArrayList<>();
        this.fetch(root, siblings);
        int ret = this.insert(siblings);

        this.used = null;
        //this.keys = null;
    }

    public String replace(String word, char mask) {
        char[] temp = word.toCharArray();
        for (int i = 0, len = temp.length; i < len;) {
            List<Integer> cps = this.commonPrefixSearch(temp, i, len, 0);
            if (cps.isEmpty()) {
                ++i;
                continue;
            }
            String k = this.keys.get(cps.get(cps.size() - 1));
            for (int j = i, end = i + k.length(); j < end; ++j) {
                temp[j] = mask;
            }
            i += k.length();
        }
        return String.valueOf(temp);
    }

    public List<Integer> commonPrefixSearch(char[] key, int pos, int len, int nodePos) {
        if (null == this.base) {
            return Collections.EMPTY_LIST;
        }
        if (len <= 0) {
            len = key.length;
        }
        if (nodePos <= 0) {
            nodePos = 0;
        }

        List<Integer> result = new ArrayList<Integer>();

        int b = this.base[nodePos];
        int n;
        int p;

        for (int i = pos; i < len; i++) {
            p = b;
            n = this.base[p];

            if (b == this.check[p] && n < 0) {
                result.add(-n - 1);
            }

            p = b + (int) (key[i]) + 1;
            if (b == this.check[p]) {
                b = this.base[p];
            } else {
                return result;
            }
        }

        p = b;
        n = this.base[p];

        if (b == this.check[p] && n < 0) {
            result.add(-n - 1);
        }

        return result;
    }

    private int fetch(Node parent, List<Node> siblings) {
        if (this.error < 0) {
            return 0;
        }
        int prevCode = 0;
        for (int i = parent.left; i < parent.right; ++i) {
            String temp = this.keys.get(i);
            if (temp.length() < parent.depth) {
                continue;
            }
            int curCode = 0;
            if (temp.length() != parent.depth) {
                curCode = (int) temp.charAt(parent.depth) + 1;
            }
            if (prevCode > curCode) {
                this.error = 3;
                return 0;
            }
            if (prevCode != curCode || 0 == siblings.size()) {
                Node tempNode = new Node();
                tempNode.depth = parent.depth + 1;
                tempNode.code = curCode;
                tempNode.left = i;
                if (0 != siblings.size()) {
                    siblings.get(siblings.size() - 1).right = i;
                }
                siblings.add(tempNode);
            }
            prevCode = curCode;
        }
        if (0 != siblings.size()) {
            siblings.get(siblings.size() - 1).right = parent.right;
        }
        return siblings.size();
    }

    private int insert(List<Node> siblings) {
        if (this.error < 0) {
            return 0;
        }
        int begin = 0;
        int position = ((siblings.get(0).code + 1 > this.nextCheckPosition) ? siblings.get(0).code + 1 : this.nextCheckPosition) - 1;
        int nonZeroNum = 0;
        int first = 0;
        if (this.allocSize <= position) {
            this.resize(position + 1);
        }
        loop: while (true) {
            ++position;

            if (this.allocSize <= position) {
                this.resize(position + 1);
            }

            if (0 != this.check[position]) {
                ++nonZeroNum;
                continue;
            } else if (0 == first) {
                this.nextCheckPosition = position;
                first = 1;
            }
            begin = position - siblings.get(0).code;
            if (this.allocSize <= (begin + siblings.get(siblings.size() - 1).code)) {
                double temp = 1.0 * this.keySize / (this.process + 1);
                double fac = (1.05 > temp) ? 1.05 : temp;
                this.resize((int) (this.allocSize * fac));
            }
            if (this.used[begin]) {
                continue;
            }
            for (int i = 1, len = siblings.size(); i < len; ++i) {
                if (0 != this.check[begin + siblings.get(i).code]) {
                    continue loop;
                }
            }
            break;
        }
        if (1.0 * nonZeroNum / (position - this.nextCheckPosition + 1) >= 0.95) {
            this.nextCheckPosition = position;
        }
        this.used[begin] = true;
        int temp = begin + siblings.get(siblings.size() - 1).code + 1;
        this.size = this.size > temp ? size : temp;
        for (int i = 0, len = siblings.size(); i < len; ++i) {
            this.check[begin + siblings.get(i).code] = begin;
        }
        for (int i = 0, len = siblings.size(); i < len; ++i) {
            List<Node> newSiblings = new ArrayList<>();
            if (0 == this.fetch(siblings.get(i), newSiblings)) {
                this.base[begin + siblings.get(i).code] = -siblings.get(i).left - 1;
                ++this.process;
            } else {
                int h = this.insert(newSiblings);
                this.base[begin + siblings.get(i).code] = h;
            }
        }
        return begin;
    }

    private void resize(int newSize) {
        int[] base = new int[newSize];
        int[] check = new int[newSize];
        boolean[] used = new boolean[newSize];
        if (this.allocSize > 0) {
            System.arraycopy(this.base, 0, base, 0, this.allocSize);
            System.arraycopy(this.check, 0, check, 0, this.allocSize);
            System.arraycopy(this.used, 0, used, 0, this.allocSize);
        }
        this.base = base;
        this.check = check;
        this.used = used;
        this.allocSize = newSize;
    }
}
