import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

public class PlayerSpring implements MouseMotionListener, KeyListener {

	/**
	 * Little Wall Rock Climbing Copyright 2009 Eric McCreath GNU LGPL
	 */

	ArrayList<Spring> springs;
	ArrayList<Joint> joints;
	Points points;
	Points velocity;
	Points forces;

	XYPoint lhf, rhf, lff, rff, bf;

	Wall wall;
	Point lastpoint;
	boolean leftaction, rightaction, leftfaction, rightfaction, bodyaction;

	static final double height = 1.85;
	static final double torso = 1.0;

	XYPoint lh, le, ls, n, p, ln, lf, rh, re, rs, rn, rf;
	Spring lRad, lHum, lClav, rRad, rHum, rClav, Lum1, Lum2, lFem, lTib, rFem,
			rTib;

	public PlayerSpring(Wall w) {
		this.wall = w;
		points = new Points();
		points.add(lh = new XYPoint(-0.5, 2.0));
		points.add(le = new XYPoint(-0.3, 1.7));
		points.add(ls = new XYPoint(-0.2, 1.58));

		points.add(rh = new XYPoint(0.5, 2.0));
		points.add(re = new XYPoint(0.3, 1.7));
		points.add(rs = new XYPoint(0.2, 1.58));

		points.add(n = new XYPoint(0.0, 1.58));
		points.add(p = new XYPoint(0.0, 1.0));

		points.add(rn = new XYPoint(0.2, 0.7));
		points.add(rf = new XYPoint(0.2, 0.2));

		points.add(ln = new XYPoint(-0.2, 0.7));
		points.add(lf = new XYPoint(-0.2, 0.2));

		double k = 0.4;

		springs = new ArrayList<Spring>();

		springs.add(lClav = new Spring(0.2, k, n, ls, points));
		springs.add(lHum = new Spring(0.3, k, ls, le, points));
		springs.add(lRad = new Spring(0.33, k, le, lh, points));

		springs.add(rClav = new Spring(0.2, k, n, rs, points));
		springs.add(rHum = new Spring(0.3, k, rs, re, points));
		springs.add(rRad = new Spring(0.33, k, re, rh, points));

		springs.add(Lum1 = new Spring(0.58, k, n, p, points));
		springs.add(Lum2 = new Spring(0.58, k, p, n, points));

		springs.add(lFem = new Spring(0.44, k, p, ln, points));
		springs.add(lTib = new Spring(0.5, k, ln, lf, points));

		springs.add(rFem = new Spring(0.44, k, p, rn, points));
		springs.add(rTib = new Spring(0.5, k, rn, rf, points));

		joints = new ArrayList<Joint>();
		joints.add(new Joint(Lum2, lClav, Math.PI / 2.0, Math.PI / 2.0));
		joints.add(new Joint(Lum2, rClav, -Math.PI / 2.0, -Math.PI / 2.0));

		joints.add(new Joint(rClav, rHum, -Math.PI / 2.0, Math.PI / 2.0));
		joints.add(new Joint(rHum, rRad, 0.0, Math.PI / 2.0));

		joints.add(new Joint(lClav, lHum, -Math.PI / 2.0, Math.PI / 2.0));
		joints.add(new Joint(lHum, lRad, -Math.PI / 2.0, 0.0));

		joints.add(new Joint(Lum1, rFem, 0.1, 0.4));
		joints.add(new Joint(rFem, rTib, -0.2, 0.1));

		joints.add(new Joint(Lum1, lFem, -0.4, -0.1));
		joints.add(new Joint(lFem, lTib, -0.1, 0.2));

		points.translate(new XYPoint(5.0, 1.0));

		forces = new Points(points.size());
		velocity = new Points(points.size());
		velocity.zero();

		lhf = new XYPoint(0.0, 0.0);
		rhf = new XYPoint(0.0, 0.0);
		lff = new XYPoint(0.0, 0.0);
		rff = new XYPoint(0.0, 0.0);
		bf = new XYPoint(0.0, 0.0);

		leftaction = false;

		for (Spring s : springs) {
			XYPoint p1 = points.get(s.p1index);
			XYPoint p2 = points.get(s.p2index);
			XYPoint dif1 = s.p1distance().scale(0.6);
			XYPoint dif2 = s.p1distance().scale(-0.3);
			p2.translate(dif1);
			p1.translate(dif2);
		}

		for (Joint j : joints) {
			XYPoint v1 = j.s1.vector();
			XYPoint v2 = j.s2.vector();

			double angle = v1.angle(v2);

			if (angle < j.mina) {
				XYPoint v2r = v2.rotate(-(angle - j.mina));
				points.get(j.s2.p2index).set(points.get(j.s2.p1index).add(v2r));
			} else if (angle > j.maxa) {
				XYPoint v2r = v2.rotate((j.maxa - angle));
				points.get(j.s2.p2index).set(points.get(j.s2.p1index).add(v2r));
			}
		}
	}

	public void draw(Graphics2D of) {
		double k = 0.4;

		XYPoint lhp,rhp, hp1,hp2,hp22,hp3,hp33,hp4,hp44,hp5,ldp1,ldp2,rdp1,rdp2;
		Spring lhs,rhs,ohs,hs1,hs2,hs3,hs4,hs5,lds1,lds2,lds3,rds1,rds2,rds3;

		of.setTransform(wall.trans);
		of.setColor(Color.black);
		of.setStroke(new BasicStroke(0.01f));

		// This is the section of code you need to modify to draw a more
		// realistic climber.

		////////////////point////////////////////
		//head
		lhp = new XYPoint(n.getX()-0.15, n.getY()+0.15);
		rhp = new XYPoint(n.getX()+0.15, n.getY()+0.15);


		//hair
		hp1 = new XYPoint(lhp.getX(), lhp.getY()+0.06);
		hp2 = new XYPoint(lhp.getX()+0.05, lhp.getY()+0.06);
		hp22 = new XYPoint(lhp.getX()+0.05, lhp.getY());
		hp3 = new XYPoint(lhp.getX()+0.1, lhp.getY()+0.06);
		hp33 = new XYPoint(lhp.getX()+0.1, lhp.getY());
		hp4 = new XYPoint(lhp.getX()+0.2, lhp.getY()+0.06);
		hp44 = new XYPoint(lhp.getX()+0.2, lhp.getY());
		hp5 = new XYPoint(rhp.getX(), rhp.getY()+0.06);


		//hand
		ldp1 = new XYPoint(lh.getX()-0.06, lh.getY()+0.06);
		ldp2 = new XYPoint(lh.getX()+0.06, lh.getY()+0.06);
		rdp1 = new XYPoint(rh.getX()-0.06, rh.getY()+0.06);
		rdp2 = new XYPoint(rh.getX()+0.06, rh.getY()+0.06);


		///////////////////////String///////////////

		//head
		lhs = new Spring(0.58, k, n, lhp, points);
		rhs = new Spring(0.58, k, n, rhp, points);
		ohs = new Spring(0.58, k, lhp, rhp, points);

		//hair
		hs1 = new Spring(0.58, k, lhp,hp1, points);
		hs2 = new Spring(0.58, k, hp22, hp2, points);
		hs3 = new Spring(0.58, k, hp33, hp3, points);
		hs4 = new Spring(0.58, k, hp44, hp4, points);
		hs5 = new Spring(0.58, k, rhp, hp5, points);

		//hand
		lds1 = new Spring(0.58, k, lh, ldp1, points);
		lds2 = new Spring(0.58, k, lh, ldp2, points);
		lds3 = new Spring(0.58, k, ldp1, ldp2, points);

		rds1 = new Spring(0.58, k, rh, rdp1, points);
		rds2= new Spring(0.58, k, rh, rdp2, points);
		rds3 = new Spring(0.58, k, rdp1, rdp2, points);





		for (Spring s : springs) {
			if(s==lFem || s==rFem || s==lHum || s==rHum || s==lClav || s==rClav){
				of.setStroke(new BasicStroke(0.1f));
			}else if(s==Lum1 || s==Lum2){
				of.setStroke(new BasicStroke(0.1f));
				of.setColor(Color.green);
			}
			s.draw(of);
			of.setStroke(new BasicStroke(0.01f));
			of.setColor(Color.black);

		}

		lhs.draw(of);
		rhs.draw(of);
		ohs.draw(of);
		hs1.draw(of);
		hs2.draw(of);
		hs3.draw(of);
		hs4.draw(of);
		hs5.draw(of);
		lds1.draw(of);
		lds2.draw(of);
		lds3.draw(of);
		rds1.draw(of);
		rds2.draw(of);
		rds3.draw(of);

		of.setStroke(new BasicStroke(0.1f));
		of.setColor(Color.yellow);
		new Spring(0.58,k,lf,lf,points).draw(of);
		new Spring(0.58,k,rf,rf,points).draw(of);

		of.setStroke(new BasicStroke(0.01f));


		of.setColor(Color.red);
		lRad.draw(of);
		of.setColor(Color.blue);
		lTib.draw(of);

	}

	public void update(GameComponent canvas, Wall wall) {
		/*
		 * forces.zero(); for (XYPoint f : forces) { f.translate(new
		 * XYPoint(0.0,-0.001)); }
		 * 
		 * forces.get(points.indexOf(lh)).translate(lhf); lhf.zero();
		 * 
		 * // spring force for (Spring s : springs) {
		 * forces.get(s.p1index).translate(s.p1force());
		 * forces.get(s.p2index).translate(s.p2force()); }
		 * 
		 * // damperner for (int i = 0; i< springs.size(); i++) { Spring s =
		 * springs.get(i); XYPoint v1 = forces.get(s.p1index); XYPoint v2 =
		 * forces.get(s.p2index); XYPoint vel1 = velocity.get(s.p1index);
		 * XYPoint vel2 = velocity.get(s.p2index);
		 * 
		 * XYPoint dif = vel1.sub(vel2); XYPoint v1d = dif.scale(0.1); XYPoint
		 * v2d = dif.scale(-0.1); v1.translate(v2d); v2.translate(v1d); }
		 * 
		 * 
		 * for (int i = 0; i< velocity.size(); i++) {
		 * velocity.get(i).translate(forces.get(i)); }
		 * 
		 * 
		 * 
		 * 
		 * for (int i = 0; i< points.size(); i++) { XYPoint p = points.get(i);
		 * p.translate(velocity.get(i)); if (p.y < 0.2) { p.y = 0.2; } }
		 */

		if (lhf.length() > 0.0) {
			movelim(lhf, lh, le, ls, lRad, lHum);
		}

		if (rhf.length() > 0.0) {
			movelim(rhf, rh, re, rs, rRad, rHum);
		}

		if (lff.length() > 0.0) {
			movelim(lff, lf, ln, p, lFem, lTib);
		}

		if (rff.length() > 0.0) {
			movelim(rff, rf, rn, p, rFem, rTib);
		}

		if (bf.length() > 0.0) {
			if (canmovelim(bf, rh, re, rs, rRad, rHum)
					&& canmovelim(bf, lf, ln, p, lFem, lTib)
					&& canmovelim(bf, lh, le, ls, lRad, lHum)
					&& canmovelim(bf, rf, rn, p, rFem, rTib)) {
				domovelim(bf, rh, re, rs, rRad, rHum);
				domovelim(bf, lf, ln, p, lFem, lTib);
				domovelim(bf, lh, le, ls, lRad, lHum);
				domovelim(bf, rf, rn, p, rFem, rTib);
				p.translate(bf);
				n.translate(bf);
				ls.translate(bf);
				rs.translate(bf);
				bf.zero();
			}

		}

		/*
		 * // reset the lengths for (Spring s : springs) { XYPoint p1 =
		 * points.get(s.p1index); XYPoint p2 = points.get(s.p2index); XYPoint
		 * dif1 = s.p1distance().scale(0.6); XYPoint dif2 =
		 * s.p1distance().scale(-0.3); p2.translate(dif1); p1.translate(dif2); }
		 * 
		 * // limit the angles
		 * 
		 * for (Joint j : joints) { XYPoint v1 = j.s1.vector(); XYPoint v2 =
		 * j.s2.vector();
		 * 
		 * double angle = v1.angle(v2);
		 * 
		 * 
		 * if (angle < j.mina) { XYPoint v2r = v2.rotate(-(angle-j.mina));
		 * points.get(j.s2.p2index).set(points.get(j.s2.p1index).add(v2r)); }
		 * else if (angle > j.maxa) { XYPoint v2r = v2.rotate((j.maxa-angle));
		 * points.get(j.s2.p2index).set(points.get(j.s2.p1index).add(v2r)); } }
		 */

	}

	private boolean canmovelim(XYPoint bf, XYPoint lh, XYPoint le, XYPoint ls,
			Spring lrad, Spring lhum) {
		XYPoint lsn = ls.add(bf);
		Circle lhc = new Circle(lh, lrad.length);
		Circle lsc = new Circle(lsn, lhum.length);
		XYPoint[] inter = lhc.intersect(lsc);
		return inter != null;
	}

	private void domovelim(XYPoint bf, XYPoint lh, XYPoint le, XYPoint ls,
			Spring lrad, Spring lhum) {
		XYPoint lsn = ls.add(bf);
		Circle lhc = new Circle(lh, lrad.length);
		Circle lsc = new Circle(lsn, lhum.length);
		XYPoint[] inter = lhc.intersect(lsc);
		if (inter != null) {
			double d1 = inter[0].distance(le);
			double d2 = inter[1].distance(le);
			if (d1 < d2) {
				le.set(inter[0]);
			} else {
				le.set(inter[1]);
			}
		}
	}

	private void movelim(XYPoint lhf, XYPoint lh, XYPoint le, XYPoint ls,
			Spring lrad, Spring lhum) {
		XYPoint lhn = lh.add(lhf);
		Circle lhc = new Circle(lhn, lrad.length);
		Circle lsc = new Circle(ls, lhum.length);

		XYPoint[] inter = lhc.intersect(lsc);

		// still need to check angle constraints

		if (inter != null) {
			lh.set(lhn);
			double d1 = inter[0].distance(le);
			double d2 = inter[1].distance(le);
			if (d1 < d2) {
				le.set(inter[0]);
			} else {
				le.set(inter[1]);
			}
		}
		lhf.zero();
	}

	public void mouseDragged(MouseEvent arg0) {
	}

	public void mouseMoved(MouseEvent m) {
		// System.out.println("Mouse move");

		double scale = 0.03;

		Point p = m.getPoint();
		if (lastpoint != null) {

			double xm = scale * (p.getX() - lastpoint.getX());
			double ym = scale * -(p.getY() - lastpoint.getY());

			if (leftaction) {
				lhf.x = xm;
				lhf.y = ym;
			} else if (rightaction) {
				rhf.x = xm;
				rhf.y = ym;

			} else if (leftfaction) {
				lff.x = xm;
				lff.y = ym;

			} else if (rightfaction) {
				rff.x = xm;
				rff.y = ym;

			} else if (bodyaction) {
				bf.x = xm;
				bf.y = ym;
			}
		}
		lastpoint = p;
	}

	public void keyPressed(KeyEvent k) {
		// System.out.println("Key pressed");
		int kc = k.getKeyCode();
		if (kc == KeyEvent.VK_A) {
			leftaction = true;
		} else if (kc == KeyEvent.VK_S) {
			rightaction = true;
		} else if (kc == KeyEvent.VK_Z) {
			leftfaction = true;
		} else if (kc == KeyEvent.VK_X) {
			rightfaction = true;
		} else if (kc == KeyEvent.VK_SPACE) {
			bodyaction = true;
		}
	}

	public void keyReleased(KeyEvent k) {
		// System.out.println("Key released");
		int kc = k.getKeyCode();
		if (kc == KeyEvent.VK_A) {
			leftaction = false;
		} else if (kc == KeyEvent.VK_S) {
			rightaction = false;
		} else if (kc == KeyEvent.VK_Z) {
			leftfaction = false;
		} else if (kc == KeyEvent.VK_X) {
			rightfaction = false;
		} else if (kc == KeyEvent.VK_SPACE) {
			bodyaction = false;
		}
	}

	public void keyTyped(KeyEvent arg0) {
	}

}
