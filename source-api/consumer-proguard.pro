-keep class com.user4302.mika.source.model.** { public protected *; }
-keep class com.user4302.mika.source.online.** { public protected *; }
-keep class com.user4302.mika.source.** extends com.user4302.mika.source.MangaSource { public protected *; }

-keep class com.user4302.mika.animesource.model.** { public protected *; }
-keep class com.user4302.mika.animesource.online.** { public protected *; }
-keep class com.user4302.mika.animesource.** extends com.user4302.mika.animesource.AnimeSource { public protected *; }

-keep,allowoptimization class com.user4302.mika.util.JsoupExtensionsKt { public protected *; }
