package in.dbit.csiapp.mReqAdapter;

public class RequestListItem {

    private String mRequestID;
    private String mName;
    private String mDate;
    private String mTimeSlots;
    private String msubMissed;
    private String mReason;
    //private boolean mAccept;
    //private boolean mReject;

    public RequestListItem( String RequestID, String Name, String Date, String TimeSlots, String Missed, String Reason) {
        this.mRequestID = RequestID;
        this.mName = Name;
        this.mDate = Date;
        this.mTimeSlots = TimeSlots;
        this.msubMissed = Missed;
        this.mReason = Reason;
    }

    public String getRequestID() {
        return mRequestID;
    }

    public String getName() {
        return mName;
    }

    public String getDate() {
        return mDate;
    }

    public String getTimeSlots() {
        return mTimeSlots;
    }

    public String getsubMissed() {
        return msubMissed;
    }

    public String getReason() {
        return mReason;
    }
}
