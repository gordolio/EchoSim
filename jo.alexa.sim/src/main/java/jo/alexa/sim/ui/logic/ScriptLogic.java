package jo.alexa.sim.ui.logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import jo.alexa.sim.logic.RequestLogic;
import jo.alexa.sim.ui.data.RuntimeBean;
import jo.alexa.sim.ui.data.ScriptTransactionBean;
import jo.alexa.sim.ui.data.TestCaseBean;
import jo.alexa.sim.ui.data.TransactionBean;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class ScriptLogic
{
    public static void loadHistory(RuntimeBean runtime)
    {
        runtime.getScript().getScript().clear();
        for (TransactionBean trans : runtime.getHistory())
        {
            ScriptTransactionBean strans = new ScriptTransactionBean(trans);
            runtime.getScript().getScript().add(strans);
        }
        runtime.firePropertyChange("script", null, runtime.getScript());
    }

    public static void insertHistory(RuntimeBean runtime, int position)
    {
        for (TransactionBean trans : runtime.getHistory())
        {
            ScriptTransactionBean strans = new ScriptTransactionBean(trans);
            runtime.getScript().getScript().add(position++, strans);
        }
        runtime.firePropertyChange("script", null, runtime.getScript());
    }

    public static void clearScript(RuntimeBean runtime)
    {
        runtime.getScript().getScript().clear();
        runtime.firePropertyChange("script", null, runtime.getScript());
    }

    public static void clearResults(RuntimeBean runtime)
    {
        for (ScriptTransactionBean trans : runtime.getScript().getScript())
            trans.setActualResult(null);
        runtime.firePropertyChange("script", null, runtime.getScript());
    }

    public static void clearResults(TestCaseBean casa)
    {
        for (ScriptTransactionBean trans : casa.getScript())
            trans.setActualResult(null);
    }

    public static void run(final RuntimeBean runtime)
    {
        runtime.setScriptRunning(true);
        Thread t = new Thread("Script Run") { public void run() { doRun(runtime); } };
        t.start();
    }
    
    public static void doRun(RuntimeBean runtime)
    {
        RuntimeLogic.clearHistory(runtime);
        TransactionBean trans;
        long lastUpdate = System.currentTimeMillis();
        for (ScriptTransactionBean script : runtime.getScript().getScript())
        {
            if (script.getRequestType().equals(RequestLogic.LAUNCH_REQUEST))
                trans = RuntimeLogic.startSession(runtime);
            else if (script.getRequestType().equals(RequestLogic.SESSION_ENDED_REQUEST))
                trans = RuntimeLogic.endSession(runtime, "USER_INITIATED");
            else
                trans = RuntimeLogic.send(runtime, script.getInputText());
            script.setActualResult(trans);
            long now = System.currentTimeMillis();
            if (now - lastUpdate > 1000)
            {
                runtime.firePropertyChange("script", null, runtime.getScript());
                lastUpdate = now;
            }
        }
        runtime.setScriptRunning(false);
        runtime.firePropertyChange("script", null, runtime.getScript());
    }

    public static void load(RuntimeBean runtime, File source) throws IOException
    {
        TestCaseBean casa = doLoad(runtime, source);
        runtime.setScript(casa);
        runtime.firePropertyChange("script", null, runtime.getScript());
        RuntimeLogic.setProp("app.script", source.toString());
    }

    static TestCaseBean doLoad(RuntimeBean runtime, File source)
            throws FileNotFoundException, UnsupportedEncodingException,
            IOException
    {
        InputStream is = new FileInputStream(source);
        Reader rdr = new InputStreamReader(is, "utf-8");
        Object jtestcase;
        try
        {
            jtestcase = RuntimeLogic.mParser.parse(rdr);
        }
        catch (ParseException e)
        {
            throw new IOException(e);
        }
        rdr.close();
        TestCaseBean casa = FromJSONLogic.fromJSONTestCase(jtestcase, runtime.getApp());
        casa.setFile(source);
        return casa;
    }

    public static void saveScript(RuntimeBean runtime, File source) throws IOException
    {
        JSONObject testcase = ToJSONLogic.toJSONTestCase(runtime.getScript());
        OutputStream os = new FileOutputStream(source);
        Writer wtr = new OutputStreamWriter(os, "utf-8");
        wtr.write(testcase.toJSONString());
        wtr.close();
        RuntimeLogic.setProp("app.script", source.toString());
    }

    public static void toggleExpectations(RuntimeBean runtime,
            List<ScriptTransactionBean> sel)
    {
        for (ScriptTransactionBean trans : sel)
            if (trans.getMatchMode() == ScriptTransactionBean.MODE_CANT_REGEX)
                trans.setMatchMode(ScriptTransactionBean.MODE_MUST_MATCH);
            else
                trans.setMatchMode(trans.getMatchMode() + 1);
        runtime.firePropertyChange("script", null, runtime.getScript());
    }

    public static void deleteLines(RuntimeBean runtime,
            List<ScriptTransactionBean> sel)
    {
        runtime.getScript().getScript().removeAll(sel);
        runtime.firePropertyChange("script", null, runtime.getScript());
    }

    public static void updateExpectated(RuntimeBean runtime,
            List<ScriptTransactionBean> sel)
    {
        for (ScriptTransactionBean trans : sel)
            if ((trans.getActualResult() != null) || (trans.getActualResult().getOutputText() != null))
                trans.setOutputText(trans.getActualResult().getOutputText());
        runtime.firePropertyChange("script", null, runtime.getScript());
    }

    public static void moveUp(RuntimeBean runtime, int[] selectedIndices)
    {
        List<int[]> intervals = makeIntervals(selectedIndices);
        for (int[] interval : intervals)
            if (interval[0] > 0)
            {
                ScriptTransactionBean trans = runtime.getScript().getScript().get(interval[0] - 1);
                runtime.getScript().getScript().remove(interval[0] - 1);
                runtime.getScript().getScript().add(interval[1], trans);
            }
        runtime.firePropertyChange("script", null, runtime.getScript());
    }

    public static void moveDown(RuntimeBean runtime, int[] selectedIndices)
    {
        List<int[]> intervals = makeIntervals(selectedIndices);
        for (int[] interval : intervals)
            if (interval[1] < runtime.getScript().getScript().size() - 1)
            {
                ScriptTransactionBean trans = runtime.getScript().getScript().get(interval[1] + 1);
                runtime.getScript().getScript().remove(interval[1] + 1);
                runtime.getScript().getScript().add(interval[0], trans);
            }
        runtime.firePropertyChange("script", null, runtime.getScript());
    }

    static List<int[]> makeIntervals(int[] selectedIndices)
    {
        Arrays.sort(selectedIndices);
        List<int[]> intervals = new ArrayList<int[]>();
        int start = -1;
        int end = -1;
        for (int i = 0; i < selectedIndices.length; i++)
        {
            if (start < 0)
            {
                start = selectedIndices[i];
                end = start;
            }
            else if (selectedIndices[i] == end + 1)
                end++;
            else
            {
                intervals.add(new int[] { start, end });
                start = -1;
                end = -1;
            }
        }
        if (start >= 0)
            intervals.add(new int[] { start, end });
        return intervals;
    }
    
    public static Boolean pass(ScriptTransactionBean script)
    {
        if (script.getActualResult() == null)
            return null;
        String actual = script.getActualResult().getOutputText();
        if (actual == null)
            return null;
        String expected = script.getOutputText();
        if (expected == null)
            return null;
        switch (script.getMatchMode())
        {
            case ScriptTransactionBean.MODE_MUST_MATCH:
                return actual.equalsIgnoreCase(expected);
            case ScriptTransactionBean.MODE_CANT_MATCH:
                return !actual.equalsIgnoreCase(expected);
            case ScriptTransactionBean.MODE_DONT_CARE:
                return null;
            case ScriptTransactionBean.MODE_MUST_REGEX:
                return Pattern.matches(expected, actual);
            case ScriptTransactionBean.MODE_CANT_REGEX:
                return !Pattern.matches(expected, actual);
            default:
                throw new IllegalArgumentException("Unexpected match mode: "+script.getMatchMode());
        }
    }
    
    public static Boolean pass(TestCaseBean casa)
    {
        if (casa == null)
            return null;
        Boolean result = null;
        for (ScriptTransactionBean s : casa.getScript())
        {
            Boolean r = pass(s);
            if (r == null)
                continue;
            if (r == Boolean.FALSE)
                return Boolean.FALSE;
            result = true;
        }
        return result;
    }

    public static void setExpected(RuntimeBean runtime,
            ScriptTransactionBean script, String newValue)
    {
        script.setOutputText(newValue);       
        runtime.firePropertyChange("script", null, runtime.getScript());
    }

    public static void setScriptName(RuntimeBean runtime, String newValue)
    {
        runtime.getScript().setName(newValue);
        runtime.firePropertyChange("script", null, runtime.getScript());
    }
}
