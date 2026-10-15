package com.aurel.notes.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aurel.notes.core.designsystem.AurelTheme

@Composable
fun AurelNavigationShell(
    currentRoute: String?,
    isExpandedScreen: Boolean,
    onNavigateToRoute: (String) -> Unit,
    onQuickCaptureClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val isPrimaryRoute = AurelDestination.primaryDestinations.any { it.route == currentRoute }

    if (isExpandedScreen) {
        // Tablet / Foldable / Landscape Expanded: NavigationRail + Content
        Row(modifier = Modifier.fillMaxSize().background(AurelTheme.colors.background)) {
            NavigationRail(
                modifier = Modifier.fillMaxHeight().width(88.dp),
                containerColor = AurelTheme.colors.surface,
                header = {
                    Box(modifier = Modifier.padding(vertical = 16.dp)) {
                        FloatingActionButton(
                            onClick = onQuickCaptureClick,
                            shape = CircleShape,
                            containerColor = AurelTheme.colors.primaryAccent,
                            contentColor = Color.White,
                            elevation = FloatingActionButtonDefaults.elevation(2.dp)
                        ) {
                            Icon(Icons.Filled.Edit, contentDescription = "Quick Note")
                        }
                    }
                }
            ) {
                AurelDestination.primaryDestinations.forEach { destination ->
                    val selected = currentRoute == destination.route
                    NavigationRailItem(
                        selected = selected,
                        onClick = { onNavigateToRoute(destination.route) },
                        icon = {
                            Icon(
                                if (selected) destination.selectedIcon else destination.unselectedIcon,
                                contentDescription = destination.title
                            )
                        },
                        label = {
                            Text(
                                destination.title,
                                style = AurelTheme.typography.labelSmall
                            )
                        },
                        colors = NavigationRailItemDefaults.colors(
                            selectedIconColor = AurelTheme.colors.primaryAccent,
                            unselectedIconColor = AurelTheme.colors.secondaryMuted,
                            selectedTextColor = AurelTheme.colors.primaryAccent,
                            unselectedTextColor = AurelTheme.colors.secondaryMuted,
                            indicatorColor = AurelTheme.colors.primaryAccent.copy(alpha = 0.12f)
                        )
                    )
                }
            }

            Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                content()
            }
        }
    } else {
        // Compact Phone: Scaffold with BottomNavigationBar + Contextual FAB
        Scaffold(
            containerColor = AurelTheme.colors.background,
            bottomBar = {
                AnimatedVisibility(
                    visible = isPrimaryRoute,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    NavigationBar(
                        modifier = Modifier
                            .height(68.dp)
                            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                            .border(1.dp, AurelTheme.colors.divider, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                        containerColor = AurelTheme.colors.surface,
                        tonalElevation = 0.dp
                    ) {
                        AurelDestination.primaryDestinations.forEach { destination ->
                            val selected = currentRoute == destination.route
                            NavigationBarItem(
                                selected = selected,
                                onClick = { onNavigateToRoute(destination.route) },
                                icon = {
                                    Icon(
                                        if (selected) destination.selectedIcon else destination.unselectedIcon,
                                        contentDescription = destination.title,
                                        modifier = Modifier.size(22.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        destination.title,
                                        style = AurelTheme.typography.labelSmall
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = AurelTheme.colors.primaryAccent,
                                    unselectedIconColor = AurelTheme.colors.secondaryMuted,
                                    selectedTextColor = AurelTheme.colors.primaryAccent,
                                    unselectedTextColor = AurelTheme.colors.secondaryMuted,
                                    indicatorColor = AurelTheme.colors.primaryAccent.copy(alpha = 0.12f)
                                )
                            )
                        }
                    }
                }
            },
            floatingActionButton = {
                if (isPrimaryRoute && currentRoute != AurelDestination.Settings.route) {
                    FloatingActionButton(
                        onClick = onQuickCaptureClick,
                        shape = CircleShape,
                        containerColor = AurelTheme.colors.primaryAccent,
                        contentColor = Color.White,
                        elevation = FloatingActionButtonDefaults.elevation(4.dp)
                    ) {
                        Icon(
                            imageVector = if (currentRoute == AurelDestination.Tasks.route) Icons.Filled.Add else Icons.Filled.Edit,
                            contentDescription = "Quick Action"
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                content()
            }
        }
    }
}
