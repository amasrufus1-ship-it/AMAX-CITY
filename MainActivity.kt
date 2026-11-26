package com.amaxcity.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Blue = Color(0xFF0756C9)
private val PaleBlue = Color(0xFFEAF2FF)
private val Ink = Color(0xFF17243A)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AmaxCityApp() }
    }
}

@Composable
fun AmaxCityApp() {
    var signedIn by remember { mutableStateOf(false) }
    var tab by remember { mutableStateOf("Chats") }
    var selectedChat by remember { mutableStateOf<String?>(null) }

    MaterialTheme(colorScheme = lightColorScheme(primary = Blue, background = Color.White)) {
        if (!signedIn) {
            LoginScreen(onContinue = { signedIn = true })
        } else if (selectedChat != null) {
            ChatScreen(name = selectedChat!!, onBack = { selectedChat = null })
        } else {
            Scaffold(
                topBar = {
                    TopAppBarAmax(
                        title = if (tab == "Chats") "AMAX CITY" else tab,
                        subtitle = if (tab == "Chats") "Chat • Connect • Share" else null
                    )
                },
                bottomBar = {
                    NavigationBar(containerColor = Color.White) {
                        listOf("Chats", "Groups", "Profile").forEach { item ->
                            NavigationBarItem(
                                selected = tab == item,
                                onClick = { tab = item },
                                icon = {
                                    Icon(
                                        when (item) {
                                            "Chats" -> Icons.Default.Chat
                                            "Groups" -> Icons.Default.Groups
                                            else -> Icons.Default.Person
                                        }, contentDescription = item
                                    )
                                },
                                label = { Text(item) },
                                colors = NavigationBarItemDefaults.colors(selectedIconColor = Blue, indicatorColor = PaleBlue, selectedTextColor = Blue)
                            )
                        }
                    }
                },
                containerColor = Color(0xFFF7F9FC)
            ) { padding ->
                Box(Modifier.padding(padding).fillMaxSize()) {
                    when (tab) {
                        "Chats" -> ChatsScreen(onOpen = { selectedChat = it })
                        "Groups" -> GroupsScreen()
                        else -> ProfileScreen(onSignOut = { signedIn = false })
                    }
                }
            }
        }
    }
}

@Composable
fun TopAppBarAmax(title: String, subtitle: String?) {
    Column(Modifier.fillMaxWidth().background(Blue).padding(horizontal = 20.dp, vertical = 16.dp)) {
        Text(title, color = Color.White, fontSize = 23.sp, fontWeight = FontWeight.Bold)
        if (subtitle != null) Text(subtitle, color = Color(0xFFDCEAFF), fontSize = 12.sp)
    }
}

@Composable
fun LoginScreen(onContinue: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var createAccount by remember { mutableStateOf(false) }

    Column(
        Modifier.fillMaxSize().background(Color.White).padding(26.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(Modifier.size(88.dp).background(Blue, CircleShape), contentAlignment = Alignment.Center) {
            Text("A", color = Color.White, fontSize = 54.sp, fontWeight = FontWeight.Black)
        }
        Spacer(Modifier.height(16.dp))
        Text("AMAX CITY", fontSize = 28.sp, color = Blue, fontWeight = FontWeight.ExtraBold)
        Text("Chat • Connect • Share", color = Color.Gray, fontSize = 13.sp)
        Spacer(Modifier.height(34.dp))
        Text(if (createAccount) "Create your account" else "Welcome back", fontSize = 21.sp, fontWeight = FontWeight.Bold, color = Ink)
        Spacer(Modifier.height(18.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email address") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone number") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(Modifier.height(18.dp))
        Button(onClick = onContinue, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(14.dp)) {
            Text(if (createAccount) "Create account (demo)" else "Log in (demo)", fontSize = 16.sp)
        }
        TextButton(onClick = { createAccount = !createAccount }) {
            Text(if (createAccount) "Already have an account? Log in" else "New to AMAX CITY? Create account")
        }
        Text("Prototype preview — sign-in is not connected to a server.", color = Color.Gray, fontSize = 11.sp)
    }
}

data class ChatItem(val name: String, val message: String, val time: String, val unread: Int = 0)
private val sampleChats = listOf(
    ChatItem("Daniel", "Are we still meeting today?", "2:14 PM", 2),
    ChatItem("Miriam", "Sent you the pictures", "1:48 PM"),
    ChatItem("AMAX Updates", "Welcome to AMAX CITY!", "Yesterday", 1),
    ChatItem("Samuel", "Voice message", "Yesterday")
)

@Composable
fun ChatsScreen(onOpen: (String) -> Unit) {
    var search by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(value = search, onValueChange = { search = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Search chats") }, leadingIcon = { Icon(Icons.Default.Search, null) }, singleLine = true, shape = RoundedCornerShape(16.dp))
        Spacer(Modifier.height(14.dp))
        Text("RECENT CHATS", color = Blue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        LazyColumn {
            items(sampleChats.filter { it.name.contains(search, true) || it.message.contains(search, true) }) { chat ->
                Row(
                    Modifier.fillMaxWidth().clickable { onOpen(chat.name) }.padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(Modifier.size(50.dp).background(PaleBlue, CircleShape), contentAlignment = Alignment.Center) {
                        Text(chat.name.take(1), color = Blue, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                    Column(Modifier.weight(1f).padding(start = 12.dp)) {
                        Text(chat.name, color = Ink, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                        Text(chat.message, color = Color.Gray, fontSize = 13.sp, maxLines = 1)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(chat.time, color = Color.Gray, fontSize = 11.sp)
                        if (chat.unread > 0) {
                            Spacer(Modifier.height(5.dp))
                            Box(Modifier.background(Blue, CircleShape).padding(horizontal = 7.dp, vertical = 3.dp)) {
                                Text(chat.unread.toString(), color = Color.White, fontSize = 10.sp)
                            }
                        }
                    }
                }
                HorizontalDivider(color = Color(0xFFEEF1F5))
            }
        }
        Spacer(Modifier.weight(1f))
        Button(onClick = { onOpen("New conversation") }, modifier = Modifier.align(Alignment.End)) {
            Icon(Icons.Default.Edit, null); Spacer(Modifier.width(6.dp)); Text("New chat")
        }
    }
}

@Composable
fun ChatScreen(name: String, onBack: () -> Unit) {
    var message by remember { mutableStateOf("") }
    val sent = remember { mutableStateListOf<String>() }
    Column(Modifier.fillMaxSize().background(Color(0xFFF7F9FC))) {
        Row(Modifier.fillMaxWidth().background(Blue).padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) }
            Column(Modifier.weight(1f)) {
                Text(name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("AMAX CITY chat preview", color = Color(0xFFDCEAFF), fontSize = 11.sp)
            }
            IconButton(onClick = {}) { Icon(Icons.Default.Call, null, tint = Color.White) }
            IconButton(onClick = {}) { Icon(Icons.Default.Videocam, null, tint = Color.White) }
        }
        Column(Modifier.weight(1f).fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.Bottom) {
            sent.forEach { text ->
                Text(text, Modifier.align(Alignment.End).padding(vertical = 4.dp).background(PaleBlue, RoundedCornerShape(14.dp)).padding(12.dp), color = Ink)
            }
        }
        Row(Modifier.fillMaxWidth().background(Color.White).padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {}) { Icon(Icons.Default.Mic, contentDescription = "Voice note", tint = Blue) }
            OutlinedTextField(value = message, onValueChange = { message = it }, modifier = Modifier.weight(1f), placeholder = { Text("Write a message") }, shape = RoundedCornerShape(22.dp), singleLine = true)
            IconButton(onClick = { if (message.isNotBlank()) { sent.add(message.trim()); message = "" } }) {
                Icon(Icons.Default.Send, contentDescription = "Send", tint = Blue)
            }
        }
    }
}

@Composable
fun GroupsScreen() {
    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text("Your groups", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Ink)
        Spacer(Modifier.height(8.dp))
        Text("Group conversations will appear here.", color = Color.Gray)
        Spacer(Modifier.height(20.dp))
        Button(onClick = {}) { Icon(Icons.Default.GroupAdd, null); Spacer(Modifier.width(8.dp)); Text("Create a group") }
        Spacer(Modifier.height(12.dp))
        Text("Group creation is a visual demo in this prototype.", fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun ProfileScreen(onSignOut: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(24.dp))
        Box(Modifier.size(96.dp).background(PaleBlue, CircleShape), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Person, null, tint = Blue, modifier = Modifier.size(56.dp))
        }
        Spacer(Modifier.height(14.dp))
        Text("AMAX CITY User", fontSize = 21.sp, fontWeight = FontWeight.Bold, color = Ink)
        Text("Add your name, photo and status", color = Color.Gray, fontSize = 13.sp)
        Spacer(Modifier.height(24.dp))
        ProfileRow("Edit profile", Icons.Default.Edit)
        ProfileRow("Privacy and security", Icons.Default.Lock)
        ProfileRow("Notifications", Icons.Default.Notifications)
        Spacer(Modifier.weight(1f))
        OutlinedButton(onClick = onSignOut, modifier = Modifier.fillMaxWidth()) { Text("Log out of demo") }
    }
}

@Composable
fun ProfileRow(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(Modifier.fillMaxWidth().padding(vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = Blue)
        Spacer(Modifier.width(14.dp))
        Text(label, color = Ink)
    }
    HorizontalDivider(color = Color(0xFFEEF1F5))
}
