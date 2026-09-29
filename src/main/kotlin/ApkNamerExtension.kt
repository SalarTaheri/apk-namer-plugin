open class ApkNamerExtension {
    /** فعال یا غیرفعال کردن نام‌گذاری خودکار */
    var enabled: Boolean = true

    /** جداکننده بین بخش‌های نام فایل apk (پیش‌فرض: "_") */
    var separator: String = "_"

    /** نام پایه فایل APK (در صورت خالی بودن از rootProject.name استفاده می‌شود) */
    var baseName: String? = null

    /** الگوی سفارشی نام فایل، مثل: "{baseName}_{flavor}_v{versionName}_{date}" */
    var pattern: String? = null

    /** پیشوند افزوده شده به ابتدای نام فایل */
    var prefix: String = ""

    /** پسوند افزوده شده قبل از فرمت .apk */
    var suffix: String = ""

    /** اضافه کردن نام پایه در فرمت پیش‌فرض */
    var includeBaseName: Boolean = true

    /** اضافه کردن نام Flavor در فرمت پیش‌فرض */
    var includeFlavor: Boolean = true

    /** اضافه کردن نوع بیلد (debug/release) در فرمت پیش‌فرض */
    var includeBuildType: Boolean = true

    /** اضافه کردن versionName در فرمت پیش‌فرض */
    var includeVersionName: Boolean = true

    /** اضافه کردن versionCode در فرمت پیش‌فرض */
    var includeVersionCode: Boolean = false

    /** اضافه کردن تاریخ بیلد در فرمت پیش‌فرض */
    var includeDate: Boolean = false

    /** اضافه کردن هش گیت (Git Commit SHA) در فرمت پیش‌فرض */
    var includeGitSha: Boolean = false

    /** الگوی فرمت تاریخ (پیش‌فرض: yyyyMMdd) */
    var dateFormat: String = "yyyyMMdd"

    /** استایل تبدیل حروف (پیش‌فرض: PRESERVE) */
    var caseFormat: CaseFormat = CaseFormat.PRESERVE

    /** اعمال فقط روی بیلدتایپ‌های مشخص (اگر خالی باشد روی همه بیلدها اعمال می‌شود) */
    var targetBuildTypes: List<String> = emptyList()

    /** نادیده گرفتن بیلدتایپ‌های مشخص (مثلاً listOf("debug")) */
    var excludeBuildTypes: List<String> = emptyList()

    /** لامبدای اختصاصی برای کنترل کامل بر نام خروجی */
    var customNameResolver: ((VariantContext) -> String)? = null

    /** متد DSL برای ثبت لامبدای سفارشی */
    fun outputFileName(resolver: (VariantContext) -> String) {
        this.customNameResolver = resolver
    }
}
