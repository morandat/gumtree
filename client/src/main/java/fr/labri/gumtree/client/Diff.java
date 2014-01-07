package fr.labri.gumtree.client;

import java.io.IOException;
import java.util.Arrays;

import javax.swing.JFrame;

import fr.labri.shelly.ConverterFactory.CommaSeparated;
import fr.labri.shelly.Shelly;
import fr.labri.shelly.annotations.*;
import fr.labri.gumtree.client.ui.swing.MappingsPanel;
import fr.labri.gumtree.client.ui.web.DiffServer;
import fr.labri.gumtree.matchers.Matcher;
import fr.labri.gumtree.matchers.MatcherFactories;
import fr.labri.gumtree.tree.Tree;

public class Diff {

	@Option(flags = "m", summary = "The qualified name of the class implementing the matcher.")
	public String matcher = null;

	@Option(flags = "g", summary = "Add a new generator, can be used more than once.")
	public String[] generator = null;
	
	@Option(flags = "G", converter=CommaSeparated.class,
			summary = "Comma separated list of generators.")
	public void generators(String[] generators) {
		generator = generators;
	}
	@Option(flags = "o", summary = "web for the web-based client and swing for the swing-based client.")
	public ClientType output = ClientType.WEB;

	enum ClientType {
		WEB {
			Client newClient(Diff diff, String src, String dst) {
				return diff.new WebClient(src, dst); }},
		SWING {
			Client newClient(Diff diff, String src, String dst) {
				return diff.new SwingClient(src, dst); }};
		abstract Client newClient(Diff diff, String src, String dst);
	};

	@Command(summary = "Diff via Gumtree command line front-end")
	@Default
	@Ignore
	public void diff(String src, String dst) {
		System.out.println("output: " + output);
		System.out.println("generators: "+ Arrays.toString(generator));
		System.out.println("matcher: "+ matcher);
		System.out.println("src: " + src);
		System.out.println("dst: " + dst);;
		// output.newClient(this, src, dst).start();
	}

//	class TreeFactory implements ConverterFactory {
//		@Override
//		public Converter<?> getConverter(Class<?> type) {
//			if (type.isAssignableFrom(String.class))
//				return new Converter.SimpleConverter<Tree>() {
//					@Override
//					public Tree convert(String value) {
//						try {
//							Tree t = TreeGeneratorRegistry.getInstance().getTree(value, generator);
//							return t;
//						} catch (IOException e) {
//							e.printStackTrace();
//						}
//						return null;
//					}
//				};
//			return null;
//		}
//	}

	public static void main(String[] args) throws Exception {
		if (true)
			Shelly.createShell(Diff.class).loop(System.in);
		Shelly.createCommandLine(Diff.class).parseCommandLine(args);
	}

	abstract class Client {
		final String src, dst; // This is boilerplate !!
		Client(String src, String dst) {
			this.src = src;
			this.dst = dst;
		}
		public abstract void start();
	}
	
	class WebClient extends Client {
		WebClient(String src, String dst) {
			super(src, dst);
		}

		public void start() {
			DiffServer server = new DiffServer(src, dst);
			DiffServer.start(server);
		}
	}
	class SwingClient extends Client {
		SwingClient(String src, String dst) {
			super(src, dst);
		}
		@Override
		public void start() {
			final Matcher matcher = getMatcher(getTree(src), getTree(dst));
			javax.swing.SwingUtilities.invokeLater(new Runnable() { public void run() {
				createAndShowGUI(matcher);
			} });
		}
		private void createAndShowGUI(Matcher m) {
			JFrame frame = new JFrame("GumTree");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.add(new MappingsPanel(src, dst, m.getSrc(), m.getDst(), m));
			frame.pack();
			frame.setVisible(true);
		}
		protected Matcher getMatcher(Tree src, Tree dst) {
			Matcher m = (matcher == null)
						? MatcherFactories.newMatcher(src, dst)
						: MatcherFactories.newMatcher(src,dst, matcher);
			m.match();
			return m;
		}
		protected Tree getTree(String file) {
			try {
				Tree t = TreeGeneratorRegistry.getInstance().getTree(file, generator);
				return t;
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
}
