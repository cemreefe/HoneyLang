import java.util.*;

class LangPack {

	/**
	 * TODO: read these dictionaries from files.
	 * write a function to initialize maps from json/csv files.
	*/

	public static Map<String, String> lang_Tr = new HashMap<String, String>() {
        /**
        *
        */
        private static final long serialVersionUID = -7937587330798333730L;

        {
		put("translate to tr",					"Bu cümleyi Türkiye Türkçesi'ne çeviriniz.");
		put("translate to az",					"Bu cümleyi Azerbaycan Türkçesi'ne çeviriniz.");
		put("translate to kz",					"Bu cümleyi Kazakistan Türkçesi'ne çeviriniz.");
        put("choose lesson",					"^Listeden bir ders seçiniz. (Sadece dersin numarasını giriniz.)");
        put("lesson",							"Ders ");
		put("success",							"Egzersizi tamamladınız, tebrikler!");
		put("right answer",						"Tebrikler, doğru cevap!");
		put("wrong answer",						"Yanlış cevap, doğrusu: ");
		put("welcome",							"Hoş geldin ");
        put("enter username",					"Kullanıcı adınızı girin.");
		put("enter password",					"Şifrenizi girin.");
		put("incorrect password",				"Yanlış şifre.");
		put("let's learn",						"Bu kelimeyi öğrenelim: ");
		put("ignored",							"Bu kelimeyi bir daha görmeyeceksin: ");
		put("enter an integer value",			"Lütfen eklemek istediğiniz kelimenin numarasını giriniz. ");
		put("press enter to continue",			"Devam etmek için enter'a basınız. ");
		
	}};
	
	public static Map<String, String> lang_Az = new HashMap<String, String>() {
        /**
        *
        */
        private static final long serialVersionUID = 3487871556303858955L;

        {
		put("translate to tr",					"Bu cümləni Azərbaycan türk dilinə tərcümə edin.");
		put("translate to az",					"Bu cümləni Azərbaycan türkcəsinə tərcümə edin.");
		put("translate to kz",					"Bu cümləni qazax türkcəsinə tərcümə edin.");
        put("choose lesson",					"^Siyahıdan bir kurs seçin. (Sadəcə kurs nömrəsini daxil edin.)");
        put("lesson",							"Ders ");
		put("success",							"Məşqi başa vurdunuz, afərin!");
		put("right answer",						"Afərin, düzgün cavab!");
		put("wrong answer",						"Yanlış cavab, əslində: ");
		put("welcome",							"Xoş gəldin ");
        put("enter username",					"İstifadəçi adınızı daxil edin.");
		put("enter password",					"Parolunuzu daxil edin.");
		put("incorrect password",				"Yanlış parol.");
		put("let's learn",						"Bu sözü öyrənək:");
		put("ignored",							"Bu sözü bir daha görməyəcəksiniz: ");
		put("enter an integer value",			"Əlavə etmək istədiyiniz sözün nömrəsini daxil edin. "); //TODO:
		put("press enter to continue",			"Davam etmək üçün Enter düyməsini basın.");
		
	}};
	
	public static Map<String, String> lang_Kz = new HashMap<String, String>() {
        /**
        *
        */
        private static final long serialVersionUID = -2940668197773145814L;

        {
		put("translate to tr",					"Түркия осы сөйлемді бұраңыз.");
		put("translate to az",					"Бұл сөйлемді әзірбайжан түрікшесіне аударыңыз.");
		put("translate to kz",					"Осы сөйлемді қазақ түрікшесіне аударыңыз.");
        put("choose lesson",					"^Тізімнен курс таңдаңыз. (Курстың нөмірін енгізіңіз.)");
        put("lesson",							"Курс ");
		put("success",							"Сіз жаттығуды аяқтадыңыз, құттықтаймыз!");
		put("right answer",						"Құттықтаймыз, дұрыс жауап!");
		put("wrong answer",						"Қате жауап, нақты: ");
		put("welcome",							"Қош келдіңіздер ");
        put("enter username",					"Пайдаланушы атын енгізіңіз.");
		put("enter password",					"Құпия сөзді енгізіңіз.");
		put("incorrect password",				"Қате пароль.");
		put("let's learn",						"Осы сөзді білейік: ");
		put("ignored",							"Бұл сөзді енді ешқашан көрмейсіз: ");
		put("enter an integer value",			"Қосқыңыз келетін сөздің нөмірін енгізіңіз."); //TODO:
		put("press enter to continue",			"Жалғастыру үшін enter пернесін басыңыз.");
		
	}};

	public static Map<String, String> commands = new HashMap<String, String>() {

        /**
		 *
		 */
		private static final long serialVersionUID = 1L;

		{
		put("exit",					"c::exit");

		
	}};

}