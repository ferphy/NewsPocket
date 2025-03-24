package com.example.newsapp.article.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.newsapp.core.domain.Article
import com.example.newsapp.core.presentation.ui.theme.NewsAppTheme
import org.koin.androidx.compose.koinViewModel

@Composable

fun ArticleScreenCore(
    viewModel: ArticleViewModel = koinViewModel(),
    articleId: String
) {
    LaunchedEffect(key1 = Unit) {
        viewModel.onAction(ArticleAction.LoadArticle(articleId))
    }
    ArticleScreen(
        state = viewModel.state,
        onAction = viewModel::onAction
    )
}

@Composable
private fun ArticleScreen(
    state: ArticleState,
    onAction: (ArticleAction) -> Unit
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            if (state.isLoading && state.article == null) {
                CircularProgressIndicator()
            }

            if (state.isError && state.article == null) {
                Text(
                    text = "Can't Load the Article",
                    fontSize = 27.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.error
                )
            }

            state.article?.let {
                ArticleDetail(article = state.article)
            }

        }
    }

}

@Composable
fun ArticleDetail(
    article: Article
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(vertical = 22.dp)
    ){
        article.sourceName?.let { sourceName ->
            Text(
                text = sourceName,
                fontSize = 24.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
        Spacer(modifier = Modifier.padding(vertical = 8.dp))

        article.pubDate?.let { pubDate ->
            Text(
                text = pubDate,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
        Spacer(modifier = Modifier.padding(vertical = 8.dp))
        article.title?.let { title ->
            Text(
                text = title,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Spacer(modifier = Modifier.padding(vertical = 8.dp))
        article.imageUrl?.let { imageUrl ->
            AsyncImage(
                model = imageUrl,
                contentDescription = article.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary.copy(0.4f))
            )
        }

        Spacer(modifier = Modifier.padding(vertical = 8.dp))
        article.description?.let { description ->
            Text(
                text = description,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Spacer(modifier = Modifier.padding(vertical = 8.dp))
        HorizontalDivider()

        Spacer(modifier = Modifier.padding(vertical = 8.dp))
        article.content?.let { content ->
            Text(
                text = content,
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }


}

@Preview
@Composable
private fun ArticleScreenPreview() {
    NewsAppTheme {
        ArticleScreen(
            state = ArticleState(),
            onAction = {}
        )
    }
}