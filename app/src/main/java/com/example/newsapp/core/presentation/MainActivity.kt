package com.example.newsapp.core.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.newsapp.article.presentation.ArticleScreenCore
import com.example.newsapp.core.presentation.ui.theme.NewsAppTheme
import com.example.newsapp.news.presentation.NewsScreenCore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NewsAppTheme {
                Navigation()
            }
        }
    }
}


@Composable
fun Navigation() {

    val navController = rememberNavController()

    NavHost(
        navController=navController,
        startDestination = Screen.News
    ) {
        composable<Screen.News> {
            NewsScreenCore{
                navController.navigate(Screen.Article(it))
            }
        }

        composable<Screen.Article> { navBackStackEntry ->

            val article : Screen.Article = navBackStackEntry.toRoute()
            article.articleId
            ArticleScreenCore(articleId = article.articleId)

        }
    }
}