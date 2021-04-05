import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

class Vector {

	double x;
	double y;

	Vector(double x, double y) {
		this.x = x;
		this.y = y;
	}

}

class Object {
	Vector velocity;
	Vector acceleration;
	double x;
	double y;

	double r;
	double m;

	Object(Vector velocity, Vector acceleration, double x, double y, double m, double r) {
		this.velocity = velocity;
		this.acceleration = acceleration;
		this.x = x;
		this.y = y;
		this.m = m;
		this.r = r;
	}

	Object(Vector velocity, Vector acceleration, double x, double y, double m) {
		this.velocity = velocity;
		this.acceleration = acceleration;
		this.x = x;
		this.y = y;
		this.m = m;
	}

	Object(double x, double y) {
		this.x = x;
		this.y = y;
	}

	Object(double x, double y, double m) {
		this.x = x;
		this.y = y;
		this.m = m;
	}

}

public class Orbit02 extends JFrame {
	static PaintComponent parts;

	public Orbit02() {
		super("Orbit");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		int weight = 600;
		int hight = 600;
		setSize(weight, hight);
		setLocation(400, 110);

		JPanel panel = new JPanel(new BorderLayout());
		setContentPane(panel);
		panel.setLocation(0, 0);

		parts = new PaintComponent();
		parts.setSize(weight, hight);
		panel.add(parts);
	}

	public static void main(String[] args) {
		Orbit02 orbit = new Orbit02();
		orbit.setVisible(true);
		int speed = 50;
		parts.systemComplete(parts.system);
		Timer timer = new Timer(1000 / speed, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < parts.system.length; i++) {
					move(parts.system, parts.system[i]);
				}
				for (int i = 0; i < parts.system.length; i++) {
					step(parts.system[i]);
				}
				impact(parts.system);
				parts.repaint();
			}

			void move(Object[] system, Object object) {
				if (object == null) {
					return;
				}
				object.acceleration.x = 0;
				object.acceleration.y = 0;
				for (int i = 0; i < system.length; i++) {
					if (object.equals(system[i]) || object == null || system[i] == null) {
						continue;
					}
					double dx = system[i].x - object.x;
					double dy = system[i].y - object.y;
					double p = system[i].m / Math.pow(Math.sqrt(dx * dx + dy * dy), 3);
					object.acceleration.x += dx * p;
					object.acceleration.y += dy * p;
				}
				object.velocity.x += object.acceleration.x;
				object.velocity.y += object.acceleration.y;
			}

			void step(Object object) {
				if (object == null) {
					return;
				}
				object.x += object.velocity.x;
				object.y += object.velocity.y;
			}

			void impact(Object[] system) {
				for (int i = 0; i < system.length; i++) {
					for (int j = 0; j < system.length; j++) {
						if (j == i || system[i] == null || system[j] == null) {
							continue;
						}
						if (distance(system[i], system[j]) < system[i].r + system[j].r) {
							system[j].r = Math.pow(Math.pow(system[j].r, 3) + Math.pow(system[i].r, 3), 0.3333);
							double k = system[i].m / (system[i].m + system[j].m);
							system[j].x += k * (system[i].x - system[j].x);
							system[j].y += k * (system[i].y - system[j].y);
							system[j].velocity.x += k * (system[i].velocity.x - system[j].velocity.x);
							system[j].velocity.y += k * (system[i].velocity.y - system[j].velocity.y);
							system[j].acceleration.x += k * (system[i].acceleration.x - system[j].acceleration.x);
							system[j].acceleration.y += k * (system[i].acceleration.y - system[j].acceleration.y);
							system[j].m += system[i].m;
							system[i] = null;
						}
					}
				}
			}

			double distance(Object object1, Object object) {
				return Math.sqrt((object1.x - object.x) * (object1.x - object.x)
						+ (object1.y - object.y) * (object1.y - object.y));
			}
		});
		timer.start();
	}

}

class PaintComponent extends JPanel {
	Color color = Color.BLUE;
	public int n = 14;
	public int p = 30;
	public double r;

	public Object[] system = new Object[n * n + 1];

	double distance(Object object1, Object object) {
		return Math.sqrt(
				(object1.x - object.x) * (object1.x - object.x) + (object1.y - object.y) * (object1.y - object.y));
	}

	public Random rand = new Random();
	Vector velocitySearch(Object star, Object object) {
		if (star==null || star.equals(object)){
			return new Vector(0,0);
		}
		double dx = star.x - object.x;
		double dy = star.y - object.y;
		double k = Math.sqrt(star.m / Math.pow(distance(star, object), 3));
		k*=(rand.nextDouble()*0.1*(Math.sqrt(2)-1)+1); 
		
		Vector velocity = new Vector(-k * dy, k * dx);
		return velocity;
	}

	public void systemComplete(Object[] system) {
		int x =100;
		int y =100;
		system[system.length - 1] = new Object(new Vector(0, 0), new Vector(0, 0), x+p*n/2-p/2, y+p*n/2-p/2, 100, 3);
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				system[n * i + j] = new Object(new Vector(0, 0), new Vector(0, 0), x + p * i, y + p * j, 0.01, 2);
				system[n*i+j].velocity = velocitySearch(system[system.length-1], system[n*i+j]);
			}
		}
//		for (int i = 0; i < system.length; i++) {
//			system[i].velocity = velocitySearch(system[system.length-1], system[i]);
//		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.WHITE);

		Rectangle2D.Float background = new Rectangle2D.Float(0, 0, getWidth(), getHeight());
		g2d.fill(background);

		g2d.setColor(Color.GRAY);

		for (int i = 0; i < system.length; i++) {
			if (system[i] == null) {
				continue;
			}
			if (i == system.length-1) {
				g2d.setColor(Color.YELLOW);
			}
			double x0 = system[i].x;
			double y0 = system[i].y;
			r = system[i].r;

			system[i].x = Math.round(system[i].x);
			system[i].y = Math.round(system[i].y);
			g2d.fillOval((int) Math.round((system[i].x) - system[i].r), (int) Math.round((system[i].y) - system[i].r),
					(int)(2 * r), (int)(2 * r));
			system[i].x = x0;
			system[i].y = y0;
		}
	}
}
