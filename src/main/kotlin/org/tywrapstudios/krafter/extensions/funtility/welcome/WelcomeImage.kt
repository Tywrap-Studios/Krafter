package org.tywrapstudios.krafter.extensions.funtility.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.tywrapstudios.krafter.getDataDirectory
import java.io.File
import javax.imageio.ImageIO
import kotlin.io.path.createDirectories

@Composable
@Suppress("FunctionNaming")
fun WelcomeImage(username: String, avatar: ImageBitmap) {
	Box(
		modifier = Modifier
			.fillMaxSize()
			.background(
				Brush.linearGradient(
					0.0f to Color(252, 187, 109, 255),
					0.2f to Color(216, 115, 127, 255),
					0.4f to Color(171, 108, 178, 255),
					0.6f to Color(166, 126, 227, 255),
					1.0f to Color(132, 174, 234, 255),
					start = Offset(0.0f, 150.0f),
					end = Offset.Infinite
				)
			)
			.clip(RoundedCornerShape(48.dp)),
		contentAlignment = Alignment.CenterStart
	) {
		Row(verticalAlignment = Alignment.CenterVertically) {
			Box(
				modifier = Modifier
					.width(60.dp)
			)
			Image(
				bitmap = avatar,
				contentDescription = "User Avatar",
				modifier = Modifier
					.size(260.dp)
					.padding(16.dp)
					.clip(CircleShape)
			)
			Box(
				Modifier
					.width(32.dp)
			)
			Text(
				text = "Welcome,\n$username!",
				fontWeight = FontWeight.Bold,
				fontSize = 48.sp,
				fontFamily = FontFamily.Monospace,
				modifier = Modifier.padding(16.dp)
			)
		}
	}
}

fun generateWelcomeImage(username: String, avatar: ImageBitmap): File {
	val scene = ImageComposeScene(
		width = 800,
		height = 500
	)
	scene.setContent {
		WelcomeImage(username, avatar)
	}

	val imageBitmap = scene.render().toComposeImageBitmap()

	val imageFile = File(getDataDirectory().resolve("ext-welcome").createDirectories().toFile(), "welcome-$username.png")
	ImageIO.write(imageBitmap.toAwtImage(), "png", imageFile)

	return imageFile
}
