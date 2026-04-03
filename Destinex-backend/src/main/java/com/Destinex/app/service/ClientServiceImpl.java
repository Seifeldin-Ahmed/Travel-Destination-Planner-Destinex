package com.Destinex.app.service;

import com.Destinex.app.dao.ClientDao;
import com.Destinex.app.dto.input.SignupRequest;
import com.Destinex.app.entity.Client;
import com.Destinex.app.entity.Role;

import com.Destinex.app.security.utils.MyUserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ClientServiceImpl implements ClientService{

    private final ClientDao  clientDao;
    private final BCryptPasswordEncoder passwordEncoder;

    public ClientServiceImpl(ClientDao clientDao,  BCryptPasswordEncoder passwordEncoder) {
        this.clientDao = clientDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Client findClientByEmail(String email) {
        return clientDao.findClientByEmail(email);
    }

    @Override
    public Client findClientById(Integer id) {
        return clientDao.findClientById(id);
    }

    @Override
    @Transactional
    public void save(SignupRequest signedUser) {
        Client newClient = new Client();
        newClient.setEmail(signedUser.getEmail());
        newClient.setPassword(passwordEncoder.encode(signedUser.getPassword()));
        newClient.setEnabled(true);
        newClient.setFirstName(signedUser.getFirstName());
        newClient.setLastName(signedUser.getLastName());
        newClient.setAddress(signedUser.getAddress());
        newClient.setPhoneNumber(signedUser.getPhoneNumber());
        Role clientRole = new Role("ROLE_USER");
		clientRole.setId(1);
        newClient.addRole(clientRole);
        clientDao.save(newClient);
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Client theClient = clientDao.findClientByEmail(email);
        if (theClient == null) {
            throw new UsernameNotFoundException("Invalid email or password.");
        }
    /*
        return new User(
                theClient.getEmail(),
                theClient.getPassword(),
                mapRolesToAuthorities(theClient.getRoles())
        );

         */
        return new MyUserDetails( theClient );
    }
/*
    private Collection<SimpleGrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();

        for (Role tempRole : roles) {
            SimpleGrantedAuthority tempAuthority = new SimpleGrantedAuthority(tempRole.getName());
            authorities.add(tempAuthority);
        }

        return authorities;
    }
    */


}
