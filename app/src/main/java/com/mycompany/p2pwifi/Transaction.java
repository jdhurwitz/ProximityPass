package com.mycompany.p2pwifi;

/**
 * Created by Jonny on 5/30/15.
 */
public class Transaction {

    private String m_phone_id;
    private String m_file_name;
    private int    m_file_size;

    private int    m_stage;
    /**
     * Values for stage:
     * 0 received   request-to-send
     * 1 sent       confirm-to-send
     * 2 sent       request-for-preview
     * 3 received   response-to-preview
     */

    public Transaction(String phone_no, String fname, int fsize)
    {
        this.m_phone_id   = phone_no;
        this.m_file_name  = fname;
        this.m_file_size  = fsize;
        this.m_stage      = 0;
    }

    /////////////////
    // Mutator
    /////////////////

    public void updateStage(int st)
    {
        this.m_stage = st;
        return;
    }

    /////////////////
    // Equality
    /////////////////

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass() )
            return false;
        final Transaction other = (Transaction) obj;
        return (this.m_phone_id == other.phone_id()
                && this.m_file_name == other.file_name()
                && this.m_file_size == other.file_size()
        );
    }

    /////////////////
    // Accessors
    /////////////////

    public int stage()
    {
        return this.m_stage;
    }

    public String phone_id()
    {
        return this.m_phone_id;
    }

    public String file_name()
    {
        return this.m_file_name;
    }

    public int file_size()
    {
        return this.m_file_size;
    }
}
