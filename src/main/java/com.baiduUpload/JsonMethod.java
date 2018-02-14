package com.baiduUpload;

public class JsonMethod {
    private ErrInfo errInfo;
    private Data data;
    private String traceid;

    public String getTraceid() {
        return traceid;
    }

    public void setTraceid(String traceid) {
        this.traceid = traceid;
    }

    public ErrInfo getErrInfo() {
        return errInfo;
    }

    public void setErrInfo(ErrInfo errInfo) {
        this.errInfo = errInfo;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class ErrInfo {
        String no;
        String msg;

        public String getNo() {
            return no;
        }

        public void setNo(String no) {
            this.no = no;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }

    public static class Data {
        private String gotoUrl;
        private String u;
        private String type;
        private String token;
        private String codeString;
        private String bduss;
        private String ptoken;

        public String getBduss() {
            return bduss;
        }

        public void setBduss(String bduss) {
            this.bduss = bduss;
        }

        public String getPtoken() {
            return ptoken;
        }

        public void setPtoken(String ptoken) {
            this.ptoken = ptoken;
        }

        public String getCodeString() {
            return codeString;
        }

        public void setCodeString(String codeString) {
            this.codeString = codeString;
        }

        public String getGotoUrl() {
            return gotoUrl;
        }

        public void setGotoUrl(String gotoUrl) {
            this.gotoUrl = gotoUrl;
        }

        public String getU() {
            return u;
        }

        public void setU(String u) {
            this.u = u;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
