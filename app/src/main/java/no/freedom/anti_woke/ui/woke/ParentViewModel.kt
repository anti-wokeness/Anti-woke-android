package no.freedom.anti_woke.ui.woke

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ParentViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Parent Fragment"
    }
    val text: LiveData<String> = _text
}