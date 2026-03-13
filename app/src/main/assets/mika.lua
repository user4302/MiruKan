mika = {}
-- UI
function mika.show_text(text)
    mp.set_property("user-data/mika/show_text", text)
end
function mika.hide_ui()
    mp.set_property("user-data/mika/toggle_ui", "hide")
end
function mika.show_ui()
    mp.set_property("user-data/mika/toggle_ui", "show")
end
function mika.toggle_ui()
    mp.set_property("user-data/mika/toggle_ui", "toggle")
end
function mika.show_subtitle_settings()
    mp.set_property("user-data/mika/show_panel", "subtitle_settings")
end
function mika.show_subtitle_delay()
    mp.set_property("user-data/mika/show_panel", "subtitle_delay")
end
function mika.show_audio_delay()
    mp.set_property("user-data/mika/show_panel", "audio_delay")
end
function mika.show_video_filters()
    mp.set_property("user-data/mika/show_panel", "video_filters")
end
function mika.show_software_keyboard()
    mp.set_property("user-data/mika/software_keyboard", "show")
end
function mika.hide_software_keyboard()
    mp.set_property("user-data/mika/software_keyboard", "hide")
end
function mika.toggle_software_keyboard()
    mp.set_property("user-data/mika/software_keyboard", "toggle")
end
-- Custom buttons
function mika.set_button_title(text)
    mp.set_property("user-data/mika/set_button_title", text)
end
function mika.reset_button_title()
    mp.set_property("user-data/mika/reset_button_title", "unused")
end
function mika.hide_button()
    mp.set_property("user-data/mika/toggle_button", "hide")
end
function mika.show_button()
    mp.set_property("user-data/mika/toggle_button", "show")
end
function mika.toggle_button()
    mp.set_property("user-data/mika/toggle_button", "toggle")
end
-- Controls
function mika.previous_episode()
    mp.set_property("user-data/mika/switch_episode", "p")
end
function mika.next_episode()
    mp.set_property("user-data/mika/switch_episode", "n")
end
function mika.pause()
    mp.set_property("user-data/mika/pause", "pause")
end
function mika.unpause()
    mp.set_property("user-data/mika/pause", "unpause")
end
function mika.pauseunpause()
    mp.set_property("user-data/mika/pause", "pauseunpause")
end
function mika.seek_by(value)
    mp.set_property("user-data/mika/seek_by", value)
end
function mika.seek_to(value)
    mp.set_property("user-data/mika/seek_to", value)
end
function mika.seek_by_with_text(value, text)
    mp.set_property("user-data/mika/seek_by_with_text", value .. "|" .. text)
end
function mika.seek_to_with_text(value, text)
    mp.set_property("user-data/mika/seek_to_with_text", value .. "|" .. text)
end
function mika.int_picker(title, name_format, start, stop, step, property)
    mp.set_property("user-data/mika/launch_int_picker", title .. "|" .. name_format ..  "|" .. start .. "|" .. stop .. "|" .. step .. "|" .. property)
end
-- Legacy
function mika.left_seek_by(value)
    mika.seek_by(-value)
end
function mika.right_seek_by(value)
    mika.seek_by(value)
end
return mika
