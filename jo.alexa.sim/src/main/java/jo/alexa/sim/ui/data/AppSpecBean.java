package jo.alexa.sim.ui.data;

public class AppSpecBean
{
    private String  mName;
    private String  mEndpoint;
    private String  mApplicationID;
    private String  mUserID;
    private String  mIntentURI;
    private String  mUtteranceURI;
    
    @Override
    public String toString()
    {
        return mName;
    }
    
    public String getName()
    {
        return mName;
    }
    public void setName(String name)
    {
        mName = name;
    }
    public String getEndpoint()
    {
        return mEndpoint;
    }
    public void setEndpoint(String endpoint)
    {
        mEndpoint = endpoint;
    }
    public String getIntentURI()
    {
        return mIntentURI;
    }
    public void setIntentURI(String intentURI)
    {
        mIntentURI = intentURI;
    }
    public String getUtteranceURI()
    {
        return mUtteranceURI;
    }
    public void setUtteranceURI(String utteranceURI)
    {
        mUtteranceURI = utteranceURI;
    }

    public String getApplicationID()
    {
        return mApplicationID;
    }

    public void setApplicationID(String applicationID)
    {
        mApplicationID = applicationID;
    }

    public String getUserID()
    {
        return mUserID;
    }

    public void setUserID(String userID)
    {
        mUserID = userID;
    }
}
