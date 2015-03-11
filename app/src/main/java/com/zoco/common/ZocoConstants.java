package com.zoco.common;

import android.os.Environment;

public class ZocoConstants {

	public static final String NAVER_SEARCH_URL = "http://openapi.naver.com/search?key=e8136cb7ee6fa4510e24cd8a81cf056e&query=all&display=10&start=1&target=book_adv&d_isbn=";
    public static final String SD_DIR=Environment.getExternalStorageDirectory().getPath() + "/";
    public static final String SUFFIX_4_IMAGE_DIR= "zoco_image_dir";
    public static final String ZOCO_IMAGE_DIR = SD_DIR + SUFFIX_4_IMAGE_DIR;


    //String msg = "ZocoChat://set//";
    public static final String PROTOCOL = "ZocoChat://";

    //enumerate behaviours!
    public static final String BEHAVIOUR_SET = "set";
    public static final String BEHAVIOUR_ASK = "ask";
    public static final String BEHAVIOUR_INIT = "init";
    public static final String BEHAVIOUR_MESSAGE = "message";
    public static final String BEHAVIOUR_FIN = "fin";
    public static final String BEHAVIOUR_CONFIRM = "confirm";
	
}
