package com.example.sharedanimation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sharedanimation.ui.theme.SharedAnimationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SharedAnimationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = androidx.compose.material.MaterialTheme.colors.background
                ) {
                    Greeting()
                }
            }
        }
    }
}

@Composable
fun Greeting() {
    val visibilityStates = remember { mutableStateMapOf<StudyRoom, Boolean>() }
        LazyColumn(
            modifier = Modifier.height(180.dp),
            verticalArrangement =  Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            items(people.people.size) { person ->
                val isVisible = visibilityStates[people.people[person]] ?: true
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                ) {
                    PersonCard(
                        modifier = Modifier.fillMaxWidth(),
                        person = people.people[person],
                        onClick = { visibilityStates[people.people[person]] = !(visibilityStates[people.people[person]] ?: true) },
                        isVisible = isVisible
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    PersonDetailsCard(
                        person = people.people[person],
                        isVisible = !isVisible,
                        onClose = { visibilityStates[people.people[person]] = !(visibilityStates[people.people[person]] ?: true) }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

            }
        }
}

object people{
    val people = listOf(
        StudyRoom(
            imageRes = R.drawable.room2,
            name = "共享自习室",
            description = "人数：520"
        ),
        StudyRoom(
            imageRes = R.drawable.room1,
            name = "备考1",
            description = "人数：120"
        ),
        StudyRoom(
            imageRes = R.drawable.room3,
            name = "备考2",
            description = "人数：200"
        )
    )
}

data class StudyRoom(
    val imageRes: Int,
    val name: String,
    val description: String
)

@Composable
fun PersonCard(
    person: StudyRoom,
    onClick: () -> Unit,
    isVisible: Boolean,
    modifier: Modifier = Modifier
) {
    SharedTransitionLayout(isVisible = isVisible, modifier = modifier) {
        Card(
            modifier = Modifier
                .width(300.dp)
                .height(180.dp)
                .clickable { onClick() },
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                // 背景图片
                Image(
                    painter = painterResource(id = person.imageRes),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
                // 叠加层：标题和人数
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = person.name,
                        color = Color.White,
                        fontSize = 18.sp,
                        style = typography.titleMedium,
                        modifier = Modifier
                            .background(Color(0x80000000)) // 半透明
                            .padding(4.dp)
                    )
                    Text(
                        text = person.description,
                        color = Color.White,
                        fontSize = 14.sp,
                        style = typography.bodySmall,
                        modifier = Modifier
                            .background(Color(0x80000000)) // 半透明
                            .padding(4.dp)
                    )
                }
            }
        }


    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SharedTransitionLayout(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val animationDuration = 600

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(durationMillis = animationDuration, easing = EaseInOutEasing)) +
                expandVertically(animationSpec = tween(durationMillis = animationDuration, easing = EaseInOutEasing)),
        exit = fadeOut(animationSpec = tween(durationMillis = animationDuration, easing = EaseInOutEasing)) +
                shrinkVertically(animationSpec = tween(durationMillis = animationDuration, easing = EaseInOutEasing)),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.animateContentSize(
                animationSpec = tween(
                    durationMillis = animationDuration,
                    easing = EaseInOutEasing
                )
            )
        ) {
            content()
        }
    }
}

private val EaseInOutEasing = CubicBezierEasing(0.42f, 0f, 0.58f, 1f)
@Composable
fun PersonDetailsCard(
    person: StudyRoom,
    isVisible: Boolean,
    onClose: () -> Unit
) {
    SharedTransitionLayout(isVisible = isVisible) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .clickable { onClose() },
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Image(
                    painter = painterResource(id = person.imageRes),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = person.name,
                    color = Color.Black,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(bottom = 4.dp),
                    style =  typography.titleMedium
                )
                Text(
                    text = person.description,
                    color = Color.Gray,
                    fontSize = 16.sp,
                    style =  typography.bodySmall
                )
            }
        }
    }
}
