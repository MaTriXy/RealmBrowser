package io.github.gjyaiya.demo;

import android.app.Activity;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import io.github.gjyaiya.demo.model.Address;
import io.github.gjyaiya.demo.model.Contact;
import io.github.gjyaiya.demo.model.RealmString;
import io.github.gjyaiya.demo.model.User;
import io.github.gjyaiya.realmbrowser.RealmBrowser;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;


public class RealmBrowserHelper {

    public Context mContext;
    public RealmBrowserHelper(Context mContext){
        this.mContext = mContext;
    }

    public void startRealmBrowser(){
        RealmBrowser.getInstance().addRealmModel(User.class, Address.class,
                RealmString.class, Contact.class);
        RealmBrowser.startRealmFilesActivity((Activity)mContext);
    }

    public void addTestData(){
        removeAllUsers();
        insertUsers(10);
    }

    private void removeAllUsers() {
        RealmConfiguration config = new RealmConfiguration.Builder(mContext).build();
        Realm realm = Realm.getInstance(config);

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.clear(User.class);
            }
        });

        realm.close();
    }

    private void insertUsers(int count) {
        RealmConfiguration config = new RealmConfiguration.Builder(mContext).build();
        Realm realm = Realm.getInstance(config);

        final List<User> userList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Address address = new Address();
            address.setLat(49.8397473);
            address.setLon(24.0233077);

            User user = new User();
            user.setName("Jon Doe " + i);
            user.setIsBlocked(Math.random() > 0.5);
            user.setAge(i);
            user.setAddress(address);

            RealmList<RealmString> emailList = new RealmList<>();
            for (int k = 0; k < 5; k++) {
                emailList.add(new RealmString("jondoe" + k + "@gmail.com"));
            }
            user.setEmailList(emailList);

            RealmList<Contact> contactList = new RealmList<>();
            for (int k = 0; k < 10; k++) {
                Contact contact = new Contact();
                contact.setId(k);
                contact.setName("Filip");
                contactList.add(contact);
            }
            user.setContactList(contactList);

            userList.add(user);
        }

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(userList);
            }
        });

        realm.close();
    }
}
