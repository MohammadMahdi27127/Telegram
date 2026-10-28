# Long-Press Context Menu Implementation

## Overview
This document describes the implementation of a complete long-press context menu for chat items in the Recamender Android app. The feature allows users to archive, delete, or perform other actions on conversations.

## Changes Made

### 1. Database Layer (Db.kt)

#### New Function: `updatePersonArchived`
**Location:** Line 653-656

```kotlin
fun updatePersonArchived(personId: String, archived: Boolean) {
    writableDatabase.update("people", ContentValues().apply { 
        put("archived", if (archived) 1 else 0) 
    }, "id=?", arrayOf(personId))
}
```

**Purpose:** Updates the `archived` field in the `people` table to mark conversations as archived or unarchived.

**Parameters:**
- `personId`: The unique identifier of the conversation/person
- `archived`: Boolean flag (true to archive, false to unarchive)

### 2. UI Layer (Screens.kt)

#### Filter Logic Update
**Location:** Line 2114-2123

Updated the `shown` filter to handle archive functionality:

```kotlin
val shown = people.filter {
    (query.isBlank() || it.name.contains(query) || it.phone.contains(query)) && when (filter) {
        "note" -> it.hasNote
        "direct" -> !it.isGroup
        "group" -> it.isGroup
        "archive" -> it.archived
        "files" -> it.lastKind?.let { kind -> 
            kind.contains("voice", ignoreCase = true) || 
            kind.contains("video", ignoreCase = true) || 
            kind.contains("image", ignoreCase = true) 
        } ?: false
        else -> !it.archived  // Default: show only non-archived conversations
    }
}
```

**Behavior:**
- **"all"** tab: Shows all conversations where `archived == false`
- **"archive"** tab: Shows only conversations where `archived == true`
- **"files"** tab: Shows conversations containing media (voice, video, image)
- **"direct"** tab: Shows direct conversations (not groups)
- **"group"** tab: Shows group conversations
- **"note"** tab: Shows conversations with notes

#### Chat Items with Context Menu
**Location:** Line 2404-2567

##### State Variables
```kotlin
var showContextMenu by remember { mutableStateOf(false) }
var deletedPersonId by remember { mutableStateOf<String?>(null) }
var snackbarMessage by remember { mutableStateOf("") }
var snackbarVisible by remember { mutableStateOf(false) }
val snackbarHostState = remember { SnackbarHostState() }
```

**Purpose:**
- `showContextMenu`: Controls the visibility of the context menu dialog
- `deletedPersonId`: Tracks which conversation is pending deletion for the undo snackbar
- `snackbarMessage`: Stores the message to display in the snackbar
- `snackbarVisible`: Controls snackbar visibility
- `snackbarHostState`: Manages snackbar lifecycle

##### Card with Combined Click
```kotlin
.combinedClickable(
    onClick = { open(person.id) },
    onLongClick = { showContextMenu = true }
)
```

**Behavior:**
- **Single click**: Opens the conversation
- **Long press**: Shows the context menu dialog

##### AlertDialog - Context Menu
**Location:** Line 2462-2556

Features:
- **RTL Support**: All text is right-aligned (`textAlign = TextAlign.End`, `horizontalAlignment = Alignment.End`)
- **Dark Mode Support**: Adapts colors based on `dark` theme flag
- **Smooth Animation**: Uses `animateContentSize(tween(200))` for smooth dialog expansion/collapse

**Options:**
1. **Archive/Unarchive Button**
   - Toggles the archived status of the conversation
   - Shows "بایگانی گفتگو" (Archive conversation) when not archived
   - Shows "خروج از بایگانی" (Exit archive) when already archived
   - Displays a confirmation snackbar

2. **Delete Button**
   - Initiates deletion with undo capability
   - Red color theme to indicate destructive action
   - Triggers the snackbar with undo option

3. **Cancel Button**
   - Closes the dialog without any action
   - Transparent background with border

##### Snackbar with Undo
**Location:** Line 2558-2577

```kotlin
if (deletedPersonId == person.id && snackbarVisible) {
    LaunchedEffect(deletedPersonId) {
        val result = snackbarHostState.showSnackbar(
            message = snackbarMessage,
            actionLabel = "بازگردانی",  // "Undo" in Persian
            duration = SnackbarDuration.Long,
            withDismissAction = true
        )
        
        when (result) {
            SnackbarResult.ActionPerformed -> {
                // User clicked undo - no deletion
                snackbarVisible = false
                deletedPersonId = null
            }
            SnackbarResult.Dismissed -> {
                // Timeout reached - actually delete
                app.db.deletePerson(person.id)
                people = app.db.people(userId)
                snackbarVisible = false
                deletedPersonId = null
            }
        }
    }
}
```

**Features:**
- **Duration**: 4-5 seconds (SnackbarDuration.Long)
- **Undo Action**: Users can undo deletion by tapping "بازگردانی" (Undo)
- **Auto-delete**: Conversation is permanently deleted if snackbar times out without user action
- **Persian Labels**: All text is in Persian (Farsi)

## Imports Added

The following imports were added to support the new functionality:

```kotlin
import androidx.compose.animation.animateContentSize
import androidx.compose.material3.Divider
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
```

## RTL (Right-to-Left) Alignment

The implementation includes full RTL support:

1. **Dialog Title**: Right-aligned with `textAlign = TextAlign.End`
2. **Column Alignment**: Right-aligned with `horizontalAlignment = Alignment.End`
3. **Buttons**: Full width with proper padding
4. **Text**: All Persian text displays correctly in RTL format

## Animation Details

1. **Dialog Animation**: `animateContentSize(animationSpec = tween(200))`
   - Smooth 200ms animation when dialog appears/disappears
   - Content size changes are animated

2. **Snackbar Animation**: Built-in Material3 animation
   - Automatic slide-up animation when displayed
   - Automatic slide-down animation when dismissed

## Theme Support

The implementation supports both light and dark themes:

- **Dark Mode**: Uses dark background colors (#1E293B) with light text
- **Light Mode**: Uses white background with dark text
- **Adaptive Colors**: Buttons and dividers adjust color based on theme

Example:
```kotlin
containerColor = if (dark) Color(0xFF1E293B) else Color.White
contentColor = if (dark) Color.White else Color(0xFF1E293B)
```

## User Experience Flow

### Archive a Conversation
1. Long-press a chat item
2. Context menu appears
3. Tap "بایگانی گفتگو" (Archive)
4. Snackbar confirms: "گفتگو بایگانی شد"
5. Conversation moves to Archive tab
6. Conversation is hidden from All tab

### Unarchive a Conversation
1. Go to Archive tab
2. Long-press a conversation
3. Context menu appears
4. Tap "خروج از بایگانی" (Unarchive)
5. Snackbar confirms: "گفتگو از بایگانی خارج شد"
6. Conversation returns to All tab

### Delete a Conversation
1. Long-press a chat item
2. Context menu appears
3. Tap "حذف گفتگو" (Delete)
4. Snackbar shows: "گفتگو حذف شد" with "بازگردانی" (Undo) button
5. **Undo (within timeout)**: Conversation is restored
6. **No action (timeout expires)**: Conversation is permanently deleted

## Database Operations

All operations are performed on the local SQLite database (`daftar.db`):

- **Archive**: Sets `archived = 1` in the `people` table
- **Unarchive**: Sets `archived = 0` in the `people` table
- **Delete**: Removes the conversation and all related data (entries, AI messages, members)

## Filtering Logic

The filter logic ensures proper display across all tabs:

```
All Tab:       archived == false
Archive Tab:   archived == true
Files Tab:     lastKind contains media (voice/video/image)
Direct Tab:    isGroup == false
Group Tab:     isGroup == true
Note Tab:      hasNote == true
```

## Testing Checklist

- [ ] Long-press on a chat item displays the context menu
- [ ] Archive option toggles conversation visibility between tabs
- [ ] Delete option shows snackbar with undo button
- [ ] Undo button restores deleted conversation
- [ ] Snackbar auto-dismisses after 4-5 seconds and deletes conversation
- [ ] Archive tab shows only archived conversations
- [ ] All tab excludes archived conversations
- [ ] RTL alignment is correct in all UI elements
- [ ] Dark mode displays correctly
- [ ] Light mode displays correctly
- [ ] All Persian text displays correctly
- [ ] Animations are smooth

## Future Enhancements

Possible improvements for future versions:

1. **Multi-select**: Allow selecting multiple conversations for bulk operations
2. **Pin Conversations**: Pin frequently used conversations to top
3. **Mute Notifications**: Mute conversation notifications without archiving
4. **Search in Archive**: Add search functionality within archived conversations
5. **Archive Duration**: Automatically unarchive after a certain period
6. **Restore from Trash**: Implement a trash/recycle bin for deleted conversations
