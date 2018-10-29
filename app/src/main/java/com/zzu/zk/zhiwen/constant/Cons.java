package com.zzu.zk.zhiwen.constant;

import android.os.Environment;

import com.zzu.zk.zhiwen.app.ZhiWen;

public class Cons {
    /*------------------------------------------------------URL constants------------------------------------------------------------*/
    public static final String debugLabel = "ZHIWEN";
    public static final String rootUrl = "么乔乔乐业丏丏丑丑丒与丗且与丕专与丒专专业丘丘丘丘丏乺么义乷久乎丏";
    public static final String register = rootUrl + "乕丟乍久乔么乏乄丝乒久乇义乓乔久乒";
    public static final String login = rootUrl + "乕丟乍久乔么乏乄丝乌乏乇义乎";
    public static final String active = rootUrl + "乕丟乍久乔么乏乄丝乁乃乔义乖久";
    public static final String logout = rootUrl + "乕丟乍久乔么乏乄丝乌乏乇乏乕乔";
    public static final String request_check_email = rootUrl + "乕丟乍久乔么乏乄丝乓久乎乄乣么久乃之乣乏乄久";
    public static final String findBackPw = rootUrl + "乕丟乍久乔么乏乄丝乆义乎乄乢乁乃之买乗";
    public static final String modifyPw = rootUrl + "乕丟乍久乔么乏乄丝乍乏乄义乆乙买乗";
    public static final String modifyUn= rootUrl + "乕丟乍久乔么乏乄丝乍乏乄义乆乙乮乁乍久";
    public static final String attentionUser= rootUrl + "乕丟乍久乔么乏乄丝乁乔乔久乎乔义乏乎乵乓久乒";
    public static final String cancleAttentionUser= rootUrl + "乕丟乍久乔么乏乄丝乃乁乎乃乌久乡乔乔久乎乔义乏乎乵乓久乒";
    public static final String insertQuestion= rootUrl + "乑丟乍久乔么乏乄丝义乎乓久乒乔乱乕久乓乔义乏乎";
    public static final String collectQuestion= rootUrl + "乑丟乍久乔么乏乄丝乃乏乌乌久乃乔乱乕久乓乔义乏乎";
    public static final String getAllQuestionsDigestBelongsToUserByPage= rootUrl + "乑丟乍久乔么乏乄丝乇久乔乡乌乌乱乕久乓乔义乏乎乓乤义乇久乓乔乢久乌乏乎乇乓乴乏乵乓久乒乢乙买乁乇久";
    public static final String cancelCollectQuestion= rootUrl + "乑丟乍久乔么乏乄丝乃乁乎乃久乌乣乏乌乌久乃乔乱乕久乓乔义乏乎";
    public static final String insertReply= rootUrl + "乒丟乍久乔么乏乄丝义乎乓久乒乔乲久乐乌乙";
    public static final String deleteReply= rootUrl + "乒丟乍久乔么乏乄丝乄久乌久乔久乲久乐乌乙";
    public static final String hotDiscuss= rootUrl + "乑丟乍久乔么乏乄丝么乏乔乤义乓乃乕乓乓";
    public static final String getAllQuestionByPage= rootUrl + "乑丟乍久乔么乏乄丝乇久乔乡乌乌乱乕久乓乔义乏乎乢乙买乁乇久";
    public static final String getQuestionDetail= rootUrl + "乑丟乍久乔么乏乄丝乇久乔乱乕久乓乔义乏乎乤久乔乁义乌";
    public static final String getQuestionReplyByPage= rootUrl + "乑丟乍久乔么乏乄丝乇久乔乱乕久乓乔义乏乎乲久乐乌乙乢乙买乁乇久";
    public static final String setCollege= rootUrl + "乕丟乍久乔么乏乄丝乓久乔乣乏乌乌久乇久";
    public static final String setMajor= rootUrl + "乕丟乍久乔么乏乄丝乓久乔乭乁乊乏乒";
    public static final String refreshPerson= rootUrl + "乕丟乍久乔么乏乄丝乒久乆乒久乓么买久乒乓乏乎";
    public static final String adopteReply= rootUrl + "乒丟乍久乔么乏乄丝乁乄乏乐乔久乲久乐乌乙";
    public static final String deleteQuestion= rootUrl + "乑丟乍久乔么乏乄丝乄久乌久乔久乱乕久乓乔义乏乎";
    public static final String isMyQues= rootUrl + "乑丟乍久乔么乏乄丝义乓乭乙乱乕久乓";
    public static final String modifyAvator= rootUrl + "乕丟乍久乔么乏乄丝乍乏乄义乆乙乡乖乁乔乏乒";
    public static final String modifyBg= rootUrl + "乕丟乍久乔么乏乄丝乍乏乄义乆乙乢乇";
    public static final String getAllCollectQuestionsDigestByPage= rootUrl + "乑丟乍久乔么乏乄丝乇久乔乡乌乌乣乏乌乌久乃乔乱乕久乓乔义乏乎乓乤义乇久乓乔乢乙买乁乇久";
    public static final String getcollQuestionDetail= rootUrl + "乑丟乍久乔么乏乄丝乇久乔乃乏乌乌乱乕久乓乔义乏乎乤久乔乁义乌";
    public static final String getAttUserByPage= rootUrl + "乑丟乍久乔么乏乄丝乇久乔乡乔乔乵乓久乒乢乙买乁乇久";
    public static final String getHotQuestionDetail= rootUrl + "乑丟乍久乔么乏乄丝乇久乔乨乏乔乱乕久乓乔义乏乎乤久乔乁义乌";
    public static final String multiSearch= rootUrl + "乑丟乍久乔么乏乄丝乍乕乌乔义乳久乁乒乃么";
    /*------------------------------------------------------URL constants------------------------------------------------------------*/

    public static final boolean pushRun= true;


    public static final String[] NAMES_OF_BOTTOM_TABS = new String[]{"首页", "分类", "", "通知", "我的"};





    /*------------------------------------------------------URI constants------------------------------------------------------------*/
    public static final int REQUEST_READ_EXTERNAL_STORAGE = 1;
    public static final int REQUEST_ADD_IMAGE = 2;
    public static final int REQUEST_WRITE_EXTERNAL_STORAGE_CAMERA_MOUNT_UNMOUNT_FILESYSTEMS = 2;
    public static final int REQUEST_TAKE_PHOTO = 1;
    public static final int REQUEST_CROP_PHOTO = 3;
    public static final int REQUEST_CHANGE_BG= 300;
    /*------------------------------------------------------URI constants------------------------------------------------------------*/





    /*------------------------------------------------------SFTP constants------------------------------------------------------------*/
    public static final String ROOT_PATH_IN_SERVER = "/root/zhiwen";

    public static final String PICS_ROOT_PATH_IN_SERVER = ROOT_PATH_IN_SERVER+"/pics";
    public static final String FILES_ROOT_PATH_IN_SERVER = ROOT_PATH_IN_SERVER+"/files";
    public static final String PICS_BASE_URL = "http://112.74.53.233:8888/p";
    public static final String AVATOR_PICS_PATH_IN_SERVER = "/avator";

    public static final String BANNER_PICS_PATH_IN_SERVER = "/banner";
    public static final String BG_PICS_PATH_IN_SERVER = "/bg";
    public static final String QUES_PICS_PATH_IN_SERVER = "/ques";
    public static final String REPLY_PICS_PATH_IN_SERVER = "/reply";



    public static final String labelPathInServer = "/label";
    public static final String coursePathInServer = "/course";
    public static final String[] BANNER_NAMES = {"p1.jpg","p2.jpg","p3.jpg"};



    public static final String label = "/label.json";
    public static final String course = "/course.json";
    public static final String SFTP = "之乁义乡乩乌义乊乕乁乎丑专丑且";
    public static final String major = "计算机科学与技术";
    public static final String college = "信息工程学院";
    /*------------------------------------------------------SFTP constants------------------------------------------------------------*/






    /*------------------------------------------------------FILE constants------------------------------------------------------------*/
    public static final String SDCard = Environment.getExternalStorageDirectory().toString();
    public static final String Internal_Dir = ZhiWen.getFileDir();
    public static final String storage_place = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ?
            SDCard : Internal_Dir;
    public static final String ROOTDir = storage_place+"/Zhi问/";
    public static final String TEMP = "temp/";
    public static final String AVATOR = "avators/";
    public static final String PERSONAL_BG = "personal_bg/";
    public static final String ATTACHMENTS = "附件/";
    public static final String[] mime_types = new String[]{"txt", "pdf", "doc", "docx", "xls", "docm", "dotm", "dotx",
            "xlsm", "xltx", "pptx", "xlsx", "ppt", "rar", "zip", "7z", "mp4", "mp3", "java", "c", "cpp", "html", "js", "css", "class", "py"};
    public static final int max_upload_file_length = 60 * 1024 * 1024;
    /*------------------------------------------------------FILE constants------------------------------------------------------------*/


    /*------------------------------------------------------SP constants------------------------------------------------------------*/

    public static final String FIRST_COME = "first_come";
    public static final String IS_LOGIN = "is_login";
    public static final Integer NOT_LOGIN = -2;
//    public static final String IS_ACTIVED = "is_actived";
//    public static final String HAS_ACTIVED = "has_actived";
    public static final String COLLEGE = "college";
    public static final String MAJOR = "major";

    public static final String AVATOR_PATH = "avator_path";
    public static final String BG_PATH = "bg_path";
    public static final String COUNT_OF_MY_QUESTIONS = "count_of_my_questions";
    public static final String NAME = "name";
    public static final String DISCUSS_NUM = "discuss_num";
    public static final String FANS_NUM = "fans_num";
    public static final String NUMS_OF_COLLECTION_QUESTIONS = "nums_of_collection_questions";
    public static final String ATTENTION_PEOPLE_NUM = "attention_people_num";
    public static final String BANNER = "banner";
    public static final String SCORE = "score";


    public static final String NEW_PRO = "NEW_PRO";
    public static final String NEW_REPLY = "NEW_REPLY";
    public static final String HAS_ATT = "HAS_ATT";
    public static final String HAS_APPLY = "HAS_APPLY";
    public static final String CANCEL_ATT = "CANCEL_ATT";
    public static final String COLL = "COLL";
    public static final String CANCLE_COLL = "CANCLE_COLL";


    /*------------------------------------------------------SP constants------------------------------------------------------------*/

    /*------------------------------------------------------OTHER constants------------------------------------------------------------*/


    /**
     * 综合院系：软件技术学院、国际学院
     */

    public static final String[] college_name = {"数学与统计学院","化学与分子工程学院","物理工程学院","信息工程学院","电气工程学院",
            "材料科学与工程学院","机械工程学院","土木工程学院","水利与环境学院","化工与能源学院","建筑学院","管理工程学院","力学与工程科学学院","生命科学学院","商学院","旅游管理学院","公共管理学院",
            "法学院（知识产权学院）","文学院","新闻与传播学院","外语学院","马克思主义学院","教育学院",
            "历史学院","信息管理学院","体育学院（校本部）","音乐学院","美术学院","书法学院","基础医学院","临床医学系","医学检验系","公共卫生学院","护理学院","药学院"};
    public static final String college_major ="{"
            + "\"数学与统计学院\":[\"金融数学\",\"统计学\",\"信息与计算科学\",\"数学与应用数学\"],"
            + "\"化学与分子工程学院\":[\"化学\",\"应用化学\"],"
            + "\"物理工程学院\":[\"电子信息科学与技术\",\"电子科学与技术\",\"测控技术与仪器\",\"应用物理学\",\"物理学\"],"
            + "\"信息工程学院\":[\"软件工程\",\"计算机科学与技术\",\"通信工程\",\"电子信息工程\"],"
            + "\"电气工程学院\":[\"生物医学工程\",\"电气工程及其自动化\",\"自动化\"],"
            + "\"材料科学与工程学院\":[\"材料科学与工程\",\"材料化学\",\"包装工程\",\"材料科学与工程（中日合办•本科）\"],"
            + "\"机械工程学院\":[\"机械设计制造及其自动化\",\"机械工程及自动化\",\"工业设计\"],"
            + "\"土木工程学院\":[\"土木工程\",\"建筑环境与能源应用工程\",\"交通工程\",\"城市地下空间工程\"],"
            + "\"水利与环境学院\":[\"水利水电工程\",\"环境工程\",\"给水排水工程\",\"地理信息系统\",\"水文与水资源工程\",\"道路桥梁与渡河工程\"],"
            + "\"化工与能源学院\":[\"化学工程与工艺\",\"制药工程\",\"过程装备与控制工程\",\"热能与动力工程\",\"环境科学\",\"安全工程\"],"
            + "\"建筑学院\":[\"建筑学\",\"城乡规划学\",\"风景园林学\",\"艺术设计(环境艺术设计方向)\"],"
            + "\"管理工程学院\":[\"物流管理\",\"电子商务\",\"工程管理\",\"工业工程\"],"
            + "\"力学与工程科学学院\":[\"安全工程\",\"工程力学\"],"
            + "\"生命科学学院\":[\"生物信息\",\"生物工程\",\"生物技术\"],"
            + "\"商学院\":[\"会计学\",\"统计学\",\"金融学\",\"国际经济与贸易\",\"经济学\",\"工商管理\"],"
            + "\"旅游管理学院\":[\"酒店管理\",\"音乐表演（空乘方向）\",\"市场营销\",\"旅游管理\"],"
            + "\"公共管理学院\":[\"公共事业管理\",\"行政管理\",\"社会工作\",\"哲学\"],"
            + "\"法学院（知识产权学院）\":[\"知识产权\",\"法学\"],"
            + "\"文学院\":[\"对外汉语\",\"汉语言文学\"],"
            + "\"新闻与传播学院\":[\"广播电视新闻学(播音与主持艺术方向)\",\"广告学\",\"新闻学\"],"
            + "\"外语学院\":[\"德语\",\"俄语\",\"日语\",\"英语\"],"
            + "\"马克思主义学院\":[\"思想政治教育学 \"],"
            + "\"教育学院\":[\"应用心理学\",\"教育学\"],"
            + "\"历史学院\":[\"考古学\",\"历史学(人文科学实验班)\"],"
            + "\"信息管理学院\":[\"档案学\",\"图书馆学\",\"信息管理与信息系统\"],"
            + "\"体育学院（校本部）\":[\"体育教育\"],"
            + "\"音乐学院\":[\"音乐学\",\"音乐表演\"],"
            + "\"美术学院\":[\"绘画\",\"艺术设计\",\"雕塑\"],"
            + "\"书法学院\":[\"篆刻\",\"书法\"],"
            + "\"基础医学院\":[\"基础医学\"],"
            + "\"临床医学系\":[\"临床医学(五年制)\",\"临床医学(七年制)\",\"医学影像学\",\"麻醉学\"],"
            + "\"医学检验系\":[\"医学检验\"],"
            + "\"公共卫生学院\":[\"预防医学\"],"
            + "\"护理学院\":[\"护理学\"],"
            + "\"药学院\":[\"药物制剂\",\"药学\"]"
            + "}";

public static final String engineercourse = "[ " +
        "    \"大学英语\", " +
        "    \"微积分\", " +
        "    \"线性代数\", " +
        "    \"概率论\", " +
        "    \"C语言程序设计\", " +
        "    \"伪代码程序设计\", " +
        "    \"算法分析与设计\", " +
        "    \"C++程序设计\", " +
        "    \"数字逻辑\", " +
        "    \"汇编语言\", " +
        "    \"微机接口\", " +
        "    \"编译原理\", " +
        "    \"操作系统\", " +
        "    \"离散数学\", " +
        "    \"数据结构\", " +
        "    \"计算机组成原理\", " +
        "    \"数据库系统原理\", " +
        "    \"软件工程导论\", " +
        "    \"计算机网络\", " +
        "    \"大学计算机基础\", " +
        "    \"计算科学导论\", " +
        "    \"移动编程\", " +
        "    \"并行计算\", " +
        "    \"网络实用技术\", " +
        "\t\"计算方法\", " +
        "    \"大学物理\" " +
        "]";
    /*------------------------------------------------------OTHER constants------------------------------------------------------------*/
}
