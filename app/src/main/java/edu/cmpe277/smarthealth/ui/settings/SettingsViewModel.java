package edu.cmpe277.smarthealth.ui.settings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SettingsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public SettingsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Settings");
    }

    public LiveData<String> getText() {
        return mText;
    }

    private MutableLiveData<String> name = new MutableLiveData<>();
    private MutableLiveData<String> dateOfBirth = new MutableLiveData<>();
    private MutableLiveData<String> weight = new MutableLiveData<>();
    private MutableLiveData<String> height = new MutableLiveData<>();
    private MutableLiveData<String> idNumber = new MutableLiveData<>();
    private MutableLiveData<String> email = new MutableLiveData<>();
    private MutableLiveData<String> phoneNumber = new MutableLiveData<>();


    public void setName(String name) {
        this.name.setValue(name);
    }

    public LiveData<String> getName() {
        return name;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth.setValue(dateOfBirth);
    }

    public MutableLiveData<String> getDateOfBirth() {
        return dateOfBirth;
    }

    public void setWeight(String weight) {
        this.weight.setValue(weight);
    }

    public LiveData<String> getWeight() {
        return weight;
    }

    public void setHeight(String height) {
        this.height.setValue(height);
    }

    public LiveData<String> getHeight() {
        return height;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber.setValue(idNumber);
    }

    public LiveData<String> getIdNumber() {
        return idNumber;
    }

    public void setEmail(String email) {
        this.email.setValue(email);
    }

    public LiveData<String> getEmail() {
        return email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber.setValue(phoneNumber);
    }

    public LiveData<String> getPhoneNumber() {
        return phoneNumber;
    }
}
