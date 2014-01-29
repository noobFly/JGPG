import static org.junit.Assert.*;
import org.junit.After;
import java.lang.management.*;
import java.io.*;
import java.net.*;
import javax.xml.xpath.*;
import org.xml.sax.InputSource;
import org.w3c.dom.NodeList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchInHeap {
  public static final Logger log = LoggerFactory.getLogger(new Object(){}.getClass().getEnclosingClass().getSimpleName());
  String jhatPort = Integer.toString(7000 + Integer.parseInt(System.getProperty("org.gradle.test.worker")));
  String jhat;
  String hprof;
  boolean isSetup = false;
  // FIXME Change to false when we have enabled searchInHeapForByte
  boolean goDown = true;

  @After
  public void searchInHeapForChar() throws IOException, InterruptedException, XPathExpressionException {
    try {
      setupSearchInHeap();
      runOQL("select escape(a.toString()) from [C a where /really_secret_string/(a.toString())");
    }
    finally {
      tearDown();
    }
  }

  // FIXME Add @After when SecurerString.secureErase(byte[]) is implemented
  public void searchInHeapForByte() throws IOException, InterruptedException, XPathExpressionException {
    try {
      setupSearchInHeap();
      runOQL("select {" +
             "  obj:b," +
             "  value:(function(b) {" +
             "    var a = '';" +
             "    for (var i = 0; i < b.length; i++) {" +
             "      a+=String.fromCharCode(b[i]);" +
             "    }" +
             "    return escape(a);" +
             "  })(b)" +
             "} " +
             "from [B b where " +
             "(function(b) {" +
             "  var a = '';" +
             "  for (var i = 0; i < b.length; i++) {" +
             "    a+=String.fromCharCode(b[i]);" +
             "  }" +
             "  return /really_secret_string/(a);" +
             "})(b)");
    }
    finally {
      tearDown();
    }
  }

  public void setupSearchInHeap() throws IOException, InterruptedException, XPathExpressionException {
    if (isSetup) {
      return;
    }
    String pid = ManagementFactory.getRuntimeMXBean().getName().replaceAll("@.*", "");
    String tmp = System.getProperty("temporaryDir");
    hprof = tmp + "/" + pid + ".hprof";
    jhat = "jhat -port " + jhatPort + " " + hprof;
    try {
      log.info("Dumping heap with jmap");
      Runtime.getRuntime().exec(new String[] {"jmap", String.format("-dump:format=b,file=%s", hprof), pid}).waitFor();

      log.info("Starting jhat");
      Process p=Runtime.getRuntime().exec(jhat);

      BufferedReader reader=new BufferedReader(new InputStreamReader(p.getInputStream()));
      String line=reader.readLine();
      while(!line.matches("Server is ready."))
      {
        line=reader.readLine();
      }
      log.info("jhat ready!");
      Thread.sleep(1000);
    }
    finally {
      isSetup = true;
    }
  }

  public void runOQL(String oql) throws IOException, InterruptedException, XPathExpressionException {
    log.info("Running OQL query...");
    log.debug("OQL query: " + oql);
    URL oqlUrl = new URL("http://localhost:" + jhatPort + "/oql/?query=" + URLEncoder.encode(oql, "UTF-8"));
    URLConnection oqlConnection = oqlUrl.openConnection();
    BufferedReader in = new BufferedReader(new InputStreamReader(oqlConnection.getInputStream()));
    String inputLine;
    String xml = "";

    while ((inputLine = in.readLine()) != null)
      xml += inputLine;
    in.close();

    NodeList nodeList;
    // What I don't do just not to add an external dependency...
    xml = xml.replaceAll("(\\w+)=([^'\"][^ ]+?)([^'\"])", "$1='$2'$3");
    String xpath = "//table[@border=1]//td";
    XPath xPath = XPathFactory.newInstance().newXPath();
    nodeList = (NodeList) xPath.evaluate(xpath, new InputSource(new StringReader(xml)), XPathConstants.NODESET);
    theAssert(nodeList.getLength());
  }

  public void tearDown() throws IOException, InterruptedException {
    if (goDown) {
      Runtime.getRuntime().exec(new String[] {"pkill", "-f", jhat}).waitFor();
      new File(hprof).delete();
    }
    else {
      goDown = true;
    }
  }

  void theAssert(int found) {
    assertEquals("Secret not found!", 0, found);
  }
}
