package com.portfolio.contactlist;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.lifecycle.Observer;
import java.util.List;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.lifecycle.ViewModelProviders;

import com.portfolio.contactlist.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainFragment extends Fragment
{
    static MainViewModel mViewModel;
    static ContactListAdapter adapter = new ContactListAdapter(R.layout.card_layout);
    private EditText contactName;
    private EditText contactPhone;
    private EditText contactEmail;
    private FloatingActionButton fab;

    //CONSTRUCTOR
    public static Fragment newInstance() { return new MainFragment(); }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        contactName = (EditText)getView().findViewById(R.id.contact_name);
        contactPhone = (EditText)getView().findViewById(R.id.contact_phone);
        contactEmail = (EditText)getView().findViewById(R.id.contact_email);
        recyclerSetup();
        observerSetup();

        fab = getView().findViewById(R.id.searchButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String name = contactName.getText().toString();
                String phone = contactPhone.getText().toString();
                String email = contactEmail.getText().toString();

                if (!name.equals("")) { mViewModel.findName(name); }
                else if (!phone.equals("")) { mViewModel.findPhone(phone); }
                else if (!email.equals("")) { mViewModel.findEmail(email); }
                else { MainActivity.toaster(getContext(), "You must enter criteria to search for"); }
                clearFields();
            }
        });
    }

    //CLEAR - Cannot call non-static MainActivity.clearFields() from static Fragment
    public void clearFields()
    {
        contactName.setText("");
        contactPhone.setText("");
        contactEmail.setText("");
        contactName.requestFocus();
    }

    public void insertContact (Contact contact)
    {
        if (!contact.getContactName().equals("") && !contact.getContactPhone().equals(""))
        {
            mViewModel.insertContact(contact);
        }
    }

    //Sorts the display adapter, not the data
    public void sort(boolean reverse) { adapter.sort(reverse); }

    //OBSERVER SETUP
    private void observerSetup()
    {
        mViewModel.getAllContacts().observe(getViewLifecycleOwner(), new Observer<List<Contact>>() {
            @Override
            public void onChanged(@Nullable final List<Contact> contacts)
            {
                adapter.setContactList(contacts);
            }
        });

        mViewModel.getSearchResults().observe(getViewLifecycleOwner(), new Observer<List<Contact>>() {
            @Override
            public void onChanged(@Nullable final List<Contact> contacts)
            {
                if (contacts.size() > 0)
                {
                    adapter.setContactList(contacts);
                }
                else
                {
                    mViewModel.findName("");
                    MainActivity.toaster(getContext(),"No matches found");
                }
            }
        });
    }

    //RECYCLER SETUP
    private void recyclerSetup()
    {
        RecyclerView recyclerView;
        recyclerView = getView().findViewById(R.id.contact_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        adapter.setOnDeleteClickListener(new ContactListAdapter.OnDeleteClickListener()
        {
            public void onClick(String name)
            {
                mViewModel.deleteContact(name);
            }
        });
    }
}