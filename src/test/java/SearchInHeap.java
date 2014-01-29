import static org.junit.Assert.*;
import org.junit.After;
import java.lang.management.*;
import java.io.*;
import java.net.*;
import javax.xml.xpath.*;
import org.xml.sax.InputSource;
import org.w3c.dom.NodeList;

public class SearchInHeap {
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
      runOQL("select a.toString() from [C a where /really_secret_string/(a.toString())");
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
      System.out.println("Dumping heap with jmap");
      Runtime.getRuntime().exec(new String[] {"jmap", String.format("-dump:format=b,file=%s", hprof), pid}).waitFor();

      System.out.println("Starting jhat");
      Process p=Runtime.getRuntime().exec(jhat);

      BufferedReader reader=new BufferedReader(new InputStreamReader(p.getInputStream()));
      String line=reader.readLine();
      while(!line.matches("Server is ready."))
      {
        line=reader.readLine();
      }
      System.out.println("jhat ready!");
      Thread.sleep(1000);
    }
    finally {
      isSetup = true;
    }
  }

  public void runOQL(String oql) throws IOException, InterruptedException, XPathExpressionException {
    System.out.println("Running OQL query...");
    URL oqlUrl = new URL("http://localhost:" + jhatPort + "/oql/?query=" + URLEncoder.encode(oql, "UTF-8"));
    URLConnection oqlConnection = oqlUrl.openConnection();
    BufferedReader in = new BufferedReader(new InputStreamReader(oqlConnection.getInputStream()));
    String inputLine;
    String xml = "";

    while ((inputLine = in.readLine()) != null)
      xml += inputLine;
    in.close();

    NodeList nodeList;
    try {
      // What I don't do just not to add an external dependency...
      xml = xml.replaceAll("(\\w+)=([^'\"][^ ]+?)([^'\"])", "$1='$2'$3");
      String xpath = "//table[@border=1]//td";
      XPath xPath = XPathFactory.newInstance().newXPath();
      nodeList = (NodeList) xPath.evaluate(xpath, new InputSource(new StringReader(xml)), XPathConstants.NODESET);
    }
    catch (javax.xml.xpath.XPathExpressionException e) {
      System.out.println(xml);
      throw e;
    }
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
