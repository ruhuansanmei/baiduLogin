package com.baiduUpload;

public class BaiduContent {
    private String errno;
    private String guid_info;
    private List[] lists;

    public String getErrno() {
        return errno;
    }

    public void setErrno(String errno) {
        this.errno = errno;
    }

    public String getGuid_info() {
        return guid_info;
    }

    public void setGuid_info(String guid_info) {
        this.guid_info = guid_info;
    }

    public List[] getLists() {
        return lists;
    }

    public void setLists(List[] lists) {
        this.lists = lists;
    }

    public static class List{
        private String fs_id;
        private String size;
        private String md5;
        private String path;
        private String server_filename;

        public String getFs_id() {
            return fs_id;
        }

        public void setFs_id(String fs_id) {
            this.fs_id = fs_id;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public String getMd5() {
            return md5;
        }

        public void setMd5(String md5) {
            this.md5 = md5;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getServer_filename() {
            return server_filename;
        }

        public void setServer_filename(String server_filename) {
            this.server_filename = server_filename;
        }
    }
}
