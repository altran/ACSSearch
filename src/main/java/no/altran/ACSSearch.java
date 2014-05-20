package no.altran;

import org.apache.commons.io.FileUtils;
import org.apache.solr.client.solrj.embedded.JettySolrRunner;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.security.ProtectionDomain;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ACSSearch {
	public static final String TARGET = "./solr";
	public static final String PREFIX = "/solr";
	public static final int PORT_NO = 8678;

	public static Logger logger = LoggerFactory.getLogger(ACSSearch.class);

	public static void main(String[] args) {
		String solrTarget = TARGET;
		String solrPortStr = System.getenv("jetty.port");
		if (solrPortStr == null) {
			solrPortStr = System.getProperty("jetty.port");
		}
		int solrPort;
		if (solrPortStr != null && !solrPortStr.isEmpty()) {
			solrPort = Integer.valueOf(solrPortStr);
			solrTarget = TARGET + "-" + solrPort;
		} else {
			solrPort = PORT_NO;
		}

		File target = new File(solrTarget);

		if (!target.isDirectory()) {
			logger.info("Solr home directory missing. Creating default.");

			ProtectionDomain protectionDomain = ACSSearch.class.getProtectionDomain();
			File location = new File(protectionDomain.getCodeSource().getLocation().getPath());

			if (location.isDirectory()) {
				File src = new File(location, "solr");
				if (src.isDirectory()) {
					try {
						FileUtils.copyDirectory(src, new File(solrTarget));
					} catch (IOException e) {
						e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
						System.exit(1);
					}
				} else {
					logger.info("No solr was found in " + location.toString());
				}
			} else {
				try {
					ZipInputStream zis = new ZipInputStream(new FileInputStream(location));
					ZipEntry e;
					while ((e = zis.getNextEntry()) != null) {
						if (e.getName().startsWith("solr")) {
							File dst = new File(e.getName());
							if (e.isDirectory()) {
								dst.mkdirs();
							} else {
								dst.getParentFile().mkdirs();
								FileOutputStream fos = new FileOutputStream(dst);
								byte[] buffer = new byte[1024];
								int len;
								while ((len = zis.read(buffer)) > 0) {
									fos.write(buffer, 0, len);
								}
								fos.close();
							}
						}
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					System.exit(1);
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(1);
				}
			}
		}

		SortedMap<ServletHolder, String> extraServlets = new TreeMap<>();

		JettySolrRunner solrJetty = new JettySolrRunner(solrTarget, PREFIX, solrPort, "solr.xml", "schema.xml", true, extraServlets);

		try {
			solrJetty.start();
		} catch (Exception e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}

		logger.info("Solr server is now running");
	}
}
