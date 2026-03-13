package com.user4302.mika.di

import android.app.Application
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.user4302.domain.track.anime.store.DelayedAnimeTrackingStore
import com.user4302.domain.track.manga.store.DelayedMangaTrackingStore
import com.user4302.mika.BuildConfig
import com.user4302.mika.core.common.storage.AndroidStorageFolderProvider
import com.user4302.mika.data.AnimeUpdateStrategyColumnAdapter
import com.user4302.mika.data.Database
import com.user4302.mika.data.DateColumnAdapter
import com.user4302.mika.data.FetchTypeColumnAdapter
import com.user4302.mika.data.MangaUpdateStrategyColumnAdapter
import com.user4302.mika.data.StringListColumnAdapter
import com.user4302.mika.data.anime.AnimeDatabase
import com.user4302.mika.data.cache.AnimeBackgroundCache
import com.user4302.mika.data.cache.AnimeCoverCache
import com.user4302.mika.data.cache.ChapterCache
import com.user4302.mika.data.cache.MangaCoverCache
import com.user4302.mika.data.download.anime.AnimeDownloadCache
import com.user4302.mika.data.download.anime.AnimeDownloadManager
import com.user4302.mika.data.download.anime.AnimeDownloadProvider
import com.user4302.mika.data.download.manga.MangaDownloadCache
import com.user4302.mika.data.download.manga.MangaDownloadManager
import com.user4302.mika.data.download.manga.MangaDownloadProvider
import com.user4302.mika.data.handlers.anime.AndroidAnimeDatabaseHandler
import com.user4302.mika.data.handlers.anime.AnimeDatabaseHandler
import com.user4302.mika.data.handlers.manga.AndroidMangaDatabaseHandler
import com.user4302.mika.data.handlers.manga.MangaDatabaseHandler
import com.user4302.mika.data.saver.ImageSaver
import com.user4302.mika.data.track.TrackerManager
import com.user4302.mika.domain.source.anime.service.AnimeSourceManager
import com.user4302.mika.domain.source.manga.service.MangaSourceManager
import com.user4302.mika.domain.storage.service.StorageManager
import com.user4302.mika.extension.anime.AnimeExtensionManager
import com.user4302.mika.extension.manga.MangaExtensionManager
import com.user4302.mika.network.JavaScriptEngine
import com.user4302.mika.network.NetworkHelper
import com.user4302.mika.source.anime.AndroidAnimeSourceManager
import com.user4302.mika.source.local.entries.anime.LocalAnimeFetchTypeManager
import com.user4302.mika.source.local.image.anime.LocalAnimeBackgroundManager
import com.user4302.mika.source.local.image.anime.LocalAnimeCoverManager
import com.user4302.mika.source.local.image.anime.LocalEpisodeThumbnailManager
import com.user4302.mika.source.local.image.manga.LocalMangaCoverManager
import com.user4302.mika.source.local.io.anime.LocalAnimeSourceFileSystem
import com.user4302.mika.source.local.io.manga.LocalMangaSourceFileSystem
import com.user4302.mika.source.manga.AndroidMangaSourceManager
import com.user4302.mika.ui.player.ExternalIntents
import data.History
import data.Mangas
import dataanime.Animehistory
import dataanime.Animes
import io.requery.android.database.sqlite.RequerySQLiteOpenHelperFactory
import kotlinx.serialization.json.Json
import kotlinx.serialization.protobuf.ProtoBuf
import nl.adaptivity.xmlutil.XmlDeclMode.Charset
import nl.adaptivity.xmlutil.core.XmlVersion
import nl.adaptivity.xmlutil.serialization.XML
import uy.kohesive.injekt.api.InjektModule
import uy.kohesive.injekt.api.InjektRegistrar
import uy.kohesive.injekt.api.addSingleton
import uy.kohesive.injekt.api.addSingletonFactory
import uy.kohesive.injekt.api.get

class AppModule(val app: Application) : InjektModule {

    override fun InjektRegistrar.registerInjectables() {
        addSingleton(app)

        val sqlDriverManga =
                AndroidSqliteDriver(
                        schema = Database.Schema,
                        context = app,
                        name = "mika.db",
                        factory =
                                if (BuildConfig.DEBUG &&
                                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
                                ) {
                                    // Support database inspector in Android Studio
                                    FrameworkSQLiteOpenHelperFactory()
                                } else {
                                    RequerySQLiteOpenHelperFactory()
                                },
                        callback =
                                object : AndroidSqliteDriver.Callback(Database.Schema) {
                                    override fun onOpen(db: SupportSQLiteDatabase) {
                                        super.onOpen(db)
                                        setPragma(db, "foreign_keys = ON")
                                        setPragma(db, "journal_mode = WAL")
                                        setPragma(db, "synchronous = NORMAL")
                                    }
                                    private fun setPragma(
                                            db: SupportSQLiteDatabase,
                                            pragma: String
                                    ) {
                                        val cursor = db.query("PRAGMA $pragma")
                                        cursor.moveToFirst()
                                        cursor.close()
                                    }
                                },
                )

        val sqlDriverAnime =
                AndroidSqliteDriver(
                        schema = AnimeDatabase.Schema,
                        context = app,
                        name = "mika.animedb",
                        factory =
                                if (BuildConfig.DEBUG &&
                                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
                                ) {
                                    // Support database inspector in Android Studio
                                    FrameworkSQLiteOpenHelperFactory()
                                } else {
                                    RequerySQLiteOpenHelperFactory()
                                },
                        callback =
                                object : AndroidSqliteDriver.Callback(AnimeDatabase.Schema) {
                                    override fun onOpen(db: SupportSQLiteDatabase) {
                                        super.onOpen(db)
                                        setPragma(db, "foreign_keys = ON")
                                        setPragma(db, "journal_mode = WAL")
                                        setPragma(db, "synchronous = NORMAL")
                                    }
                                    private fun setPragma(
                                            db: SupportSQLiteDatabase,
                                            pragma: String
                                    ) {
                                        val cursor = db.query("PRAGMA $pragma")
                                        cursor.moveToFirst()
                                        cursor.close()
                                    }
                                },
                )

        addSingletonFactory {
            Database(
                    driver = sqlDriverManga,
                    historyAdapter =
                            History.Adapter(
                                    last_readAdapter = DateColumnAdapter,
                            ),
                    mangasAdapter =
                            Mangas.Adapter(
                                    genreAdapter = StringListColumnAdapter,
                                    update_strategyAdapter = MangaUpdateStrategyColumnAdapter,
                            ),
            )
        }

        addSingletonFactory {
            AnimeDatabase(
                    driver = sqlDriverAnime,
                    animehistoryAdapter =
                            Animehistory.Adapter(
                                    last_seenAdapter = DateColumnAdapter,
                            ),
                    animesAdapter =
                            Animes.Adapter(
                                    genreAdapter = StringListColumnAdapter,
                                    update_strategyAdapter = AnimeUpdateStrategyColumnAdapter,
                                    fetch_typeAdapter = FetchTypeColumnAdapter,
                            ),
            )
        }

        addSingletonFactory<MangaDatabaseHandler> {
            AndroidMangaDatabaseHandler(
                    get(),
                    sqlDriverManga,
            )
        }

        addSingletonFactory<AnimeDatabaseHandler> {
            AndroidAnimeDatabaseHandler(
                    get(),
                    sqlDriverAnime,
            )
        }

        addSingletonFactory {
            Json {
                ignoreUnknownKeys = true
                explicitNulls = false
            }
        }
        addSingletonFactory {
            XML {
                defaultPolicy { ignoreUnknownChildren() }
                autoPolymorphic = true
                xmlDeclMode = Charset
                indent = 2
                xmlVersion = XmlVersion.XML10
            }
        }
        addSingletonFactory<ProtoBuf> { ProtoBuf }

        addSingletonFactory { ChapterCache(app, get()) }

        addSingletonFactory { MangaCoverCache(app) }
        addSingletonFactory { AnimeCoverCache(app) }
        addSingletonFactory { AnimeBackgroundCache(app) }

        addSingletonFactory { NetworkHelper(app, get()) }
        addSingletonFactory { JavaScriptEngine(app) }

        addSingletonFactory<MangaSourceManager> { AndroidMangaSourceManager(app, get(), get()) }
        addSingletonFactory<AnimeSourceManager> { AndroidAnimeSourceManager(app, get(), get()) }

        addSingletonFactory { MangaExtensionManager(app) }
        addSingletonFactory { AnimeExtensionManager(app) }

        addSingletonFactory { MangaDownloadProvider(app) }
        addSingletonFactory { MangaDownloadManager(app) }
        addSingletonFactory { MangaDownloadCache(app) }

        addSingletonFactory { AnimeDownloadProvider(app) }
        addSingletonFactory { AnimeDownloadManager(app) }
        addSingletonFactory { AnimeDownloadCache(app) }

        addSingletonFactory { TrackerManager(app) }
        addSingletonFactory { DelayedAnimeTrackingStore(app) }
        addSingletonFactory { DelayedMangaTrackingStore(app) }

        addSingletonFactory { ImageSaver(app) }

        addSingletonFactory { AndroidStorageFolderProvider(app) }

        addSingletonFactory { LocalMangaSourceFileSystem(get()) }
        addSingletonFactory { LocalMangaCoverManager(app, get()) }

        addSingletonFactory { LocalAnimeSourceFileSystem(get()) }
        addSingletonFactory { LocalAnimeBackgroundManager(app, get()) }
        addSingletonFactory { LocalAnimeCoverManager(app, get()) }
        addSingletonFactory { LocalAnimeFetchTypeManager(app, get()) }
        addSingletonFactory { LocalEpisodeThumbnailManager(app, get()) }

        addSingletonFactory { StorageManager(app, get()) }

        addSingletonFactory { ExternalIntents() }

        // Asynchronously init expensive components for a faster cold start
        ContextCompat.getMainExecutor(app).execute {
            get<NetworkHelper>()

            get<MangaSourceManager>()
            get<AnimeSourceManager>()

            get<Database>()
            get<AnimeDatabase>()

            get<MangaDownloadManager>()
            get<AnimeDownloadManager>()
        }
    }
}
