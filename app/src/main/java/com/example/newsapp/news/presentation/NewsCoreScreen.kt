package com.example.newsapp.news.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.newsapp.core.domain.Article
import com.example.newsapp.core.presentation.ui.theme.NewsAppTheme
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import org.koin.androidx.compose.koinViewModel

@Composable

fun NewsScreenCore(
    viewModel: NewsViewModel = koinViewModel(),
    onArticleClick: (String) -> Unit
) {
    NewsScreen(
        state = viewModel.state,
        onAction = viewModel::onAction,
        onArticleClick = onArticleClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewsScreen(
    state: NewsState,
    onAction: (NewsAction) -> Unit,
    onArticleClick: (String) -> Unit
) {

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        state = rememberTopAppBarState()
    )
    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                title = {
                    Text(
                        text = "The News",
                        fontSize = 33.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                },
                windowInsets = WindowInsets(
                    top = 50.dp, bottom = 8.dp
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (state.isLoading && state.articlesList.isEmpty()) {
                CircularProgressIndicator()
            }

            if (state.isError && state.articlesList.isEmpty()) {
                Text(
                    text = "Can't Load News",
                    fontSize = 27.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        if (state.articlesList.isNotEmpty()) {
            val lisState = rememberLazyListState()

            val shouldPaginate = remember {
                derivedStateOf {
                    val totalItems = lisState.layoutInfo.totalItemsCount
                    val lastVisibleIndex = lisState.layoutInfo
                        .visibleItemsInfo.lastOrNull()?.index ?: 0

                    lastVisibleIndex == totalItems - 1 && !state.isLoading
                }
            }

            LaunchedEffect(key1 = lisState) {
                snapshotFlow { shouldPaginate.value }
                    .distinctUntilChanged()
                    .filter { it }
                    .collect { onAction(NewsAction.Paginate) }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 8.dp),
                state = lisState
            ) {
                itemsIndexed(
                    items = state.articlesList,
                    key = { _, article -> article.articleId!! }
                ) { index, article ->

                    ArticleItem(
                        article = article,
                        onArticleClick = onArticleClick
                    )

                }
            }
        }

    }
}

@Composable
fun ArticleItem(
    article: Article,
    onArticleClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onArticleClick(article.articleId!!) }
            .padding(vertical = 16.dp, horizontal = 16.dp)
    ){
        Text(
            text = article.sourceName!!,
            fontSize = 22.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = article.title!!,
            fontSize = 18.sp,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.height(16.dp))

        AsyncImage(
            model = article.imageUrl,
            contentDescription = article.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .background(MaterialTheme.colorScheme.primary.copy(0.3f))
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = article.description!!,
            fontSize = 17.sp,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = article.pubDate!!,
            fontSize = 15.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
    }

}

@Preview
@Composable
private fun NewsScreenPreview() {
    NewsAppTheme {
        NewsScreen(
            state = NewsState(),
            onAction = {},
            onArticleClick = {}
        )
    }
}