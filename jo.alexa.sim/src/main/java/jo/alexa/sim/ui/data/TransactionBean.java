package jo.alexa.sim.ui.data;

import jo.alexa.sim.data.MatchBean;
import jo.alexa.sim.data.ResponseBean;

public class TransactionBean
{
    private String          mRequestType;
    private String          mInputText;
    private MatchBean       mInputMatch;
    private ResponseBean    mOutputData;
    private String          mOutputText;
    private long            mTransactionStart;
    private long            mTransactionEnd;
    private Throwable       mError;
    
    public TransactionBean()
    {
    }

    public TransactionBean(TransactionBean trans)
    {
        mRequestType = trans.mRequestType;
        mInputText = trans.mInputText;
        mInputMatch = trans.mInputMatch;
        mOutputData = trans.mOutputData;
        mOutputText = trans.mOutputText;
        mTransactionStart = trans.mTransactionStart;
        mTransactionEnd = trans.mTransactionEnd;
        mError = trans.mError;
    }
    
    public String getInputText()
    {
        return mInputText;
    }
    public void setInputText(String inputText)
    {
        mInputText = inputText;
    }
    public MatchBean getInputMatch()
    {
        return mInputMatch;
    }
    public void setInputMatch(MatchBean inputMatch)
    {
        mInputMatch = inputMatch;
    }
    public ResponseBean getOutputData()
    {
        return mOutputData;
    }
    public void setOutputData(ResponseBean outputData)
    {
        mOutputData = outputData;
    }
    public String getOutputText()
    {
        return mOutputText;
    }
    public void setOutputText(String outputText)
    {
        mOutputText = outputText;
    }
    public long getTransactionStart()
    {
        return mTransactionStart;
    }
    public void setTransactionStart(long transactionStart)
    {
        mTransactionStart = transactionStart;
    }
    public long getTransactionEnd()
    {
        return mTransactionEnd;
    }
    public void setTransactionEnd(long transactionEnd)
    {
        mTransactionEnd = transactionEnd;
    }
    public Throwable getError()
    {
        return mError;
    }
    public void setError(Throwable error)
    {
        mError = error;
    }

    public String getRequestType()
    {
        return mRequestType;
    }

    public void setRequestType(String requestType)
    {
        mRequestType = requestType;
    }
}
