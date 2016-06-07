package com.klaus.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Keans {

	private int KNumber;
	private double[] data;

	public Keans(double[] data) {

		this.data=data;
		
		this.KNumber=6;
		
	}

	public Keans(int KNumber, double[] data) {

		this.KNumber=KNumber;
		this.data=data;
		
	}

	public List<Map<String, Double>> classifyData() {

		List<Map<String, Double>> list = new ArrayList<Map<String, Double>>();

		double[][] g;

		g = cluster(data, KNumber);

		for (int i = 0; i < g.length; i++) {

			Map<String, Double> map = new HashMap<String, Double>();

			double max = 0, min = 0, sum = 0, mean = 0;

			for (int j = 0; j < g[i].length; j++) {

				if (j == 0) {

					max = g[i][j];
					min = g[i][j];

				}

				sum = sum + g[i][j];

				if (g[i][j] > max) {

					max = g[i][j];

				}

				if (g[i][j] < min) {

					min = g[i][j];

				}

				System.out.print(g[i][j]);
				System.out.print("\t");

			}

			if (g[i].length != 0) {

				mean = sum / (g[i].length + 0);

			}

			map.put("maxk", max);
			map.put("mink", min);
			map.put("meank", mean);

			list.add(map);

			System.out.println();

		}

		return list;

	}

	/*
	 * ���ຯ�����塣 ���һά double ���顣ָ��������Ŀ k�� �����ݾ۳� k �ࡣ
	 */
	private double[][] cluster(double[] p, int k) {
		// ��ž���ɵľ�������
		double[] c = new double[k];
		// ����¼���ľ�������
		double[] nc = new double[k];
		// ��ŷŻؽ��
		double[][] g;
		// ��ʼ����������
		// ���䷽�������ѡȡ k ��
		// �����в���ǰ k ����Ϊ��������
		// �������ĵ�ѡȡ��Ӱ�����ս��
		for (int i = 0; i < k; i++)
			c[i] = p[i];
		// ѭ�����࣬���¾�������
		// ���������Ĳ���Ϊֹ
		while (true) {
			// ���ݾ������Ľ�Ԫ�ط���
			g = group(p, c);
			// ��������ľ�������
			for (int i = 0; i < g.length; i++) {
				nc[i] = center(g[i]);
			}
			// ����������Ĳ�ͬ
			if (!equal(nc, c)) {
				// Ϊ��һ�ξ���׼��
				c = nc;
				nc = new double[k];
			} else // �������
				break;
		}
		// ���ؾ�����
		return g;
	}

	/*
	 * �������ĺ��� �򵥵�һά���෵��������ƽ��ֵ ����չ
	 */
	private double center(double[] p) {
		return sum(p) / p.length;
	}

	/*
	 * ���� double ������ p �;������� c�� ���� c �� p ��Ԫ�ؾ��ࡣ���ض�ά���顣 ��Ÿ���Ԫ�ء�
	 */
	private double[][] group(double[] p, double[] c) {
		// �м����������������
		int[] gi = new int[p.length];
		// ����ÿһ��Ԫ�� pi ͬ�������� cj �ľ���
		// pi �� cj �ľ�����С���Ϊ j ��
		for (int i = 0; i < p.length; i++) {
			// ��ž���
			double[] d = new double[c.length];
			// ���㵽ÿ���������ĵľ���
			for (int j = 0; j < c.length; j++) {
				d[j] = distance(p[i], c[j]);
			}
			// �ҳ���С���룬������Сֵ���±�
			int ci = min(d);
			// ���������һ��
			gi[i] = ci;
		}
		// ��ŷ�����
		double[][] g = new double[c.length][];
		// ����ÿ���������ģ�����
		for (int i = 0; i < c.length; i++) {
			// �м��������¼�����ÿһ��Ĵ�С
			int s = 0;
			// ����ÿһ��ĳ���
			for (int j = 0; j < gi.length; j++)
				if (gi[j] == i)
					s++;
			// �洢ÿһ��ĳ�Ա
			g[i] = new double[s];
			s = 0;
			// ���ݷ����ǽ���Ԫ�ع�λ
			for (int j = 0; j < gi.length; j++)
				if (gi[j] == i) {
					g[i][s] = p[j];
					s++;
				}
		}
		// ���ط�����
		return g;
	}

	/*
	 * ����������֮��ľ��룬 ���������򵥵�һάŷ�Ͼ��룬 ����չ��
	 */
	private double distance(double x, double y) {
		return Math.abs(x - y);
	}

	/*
	 * ���ظ��� double �����Ԫ��֮�͡�
	 */
	private double sum(double[] p) {
		double sum = 0.0;
		for (int i = 0; i < p.length; i++)
			sum += p[i];
		return sum;
	}

	/*
	 * ���� double �������飬������Сֵ���±ꡣ
	 */
	private int min(double[] p) {
		int i = 0;
		double m = p[0];
		for (int j = 1; j < p.length; j++) {
			if (p[j] < m) {
				i = j;
				m = p[j];
			}
		}
		return i;
	}

	/*
	 * �ж����� double �����Ƿ���ȡ� ����һ���Ҷ�Ӧλ��ֵ��ͬ�����档
	 */
	private boolean equal(double[] a, double[] b) {
		if (a.length != b.length)
			return false;
		else {
			for (int i = 0; i < a.length; i++) {
				if (a[i] != b[i])
					return false;
			}
		}
		return true;
	}
}