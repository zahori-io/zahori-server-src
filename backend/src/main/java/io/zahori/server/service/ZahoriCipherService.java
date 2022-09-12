package io.zahori.server.service;

/*-
 * #%L
 * zahori-framework
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2021 PANEL SISTEMAS INFORMATICOS,S.L
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ZahoriCipherService {

    private static final Logger LOG = LoggerFactory.getLogger(ZahoriCipherService.class);

    @Value("${ZAHORI_CIPHER_SECRET:YourSecr}")
    private String secret;

    private static final String CHARSET = "UTF8";
    private static final String ALGORITHM = "DES";
    private DESKeySpec keySpec;
    private SecretKeyFactory keyFactory;
    private SecretKey key;

    public ZahoriCipherService() {
    }

    @PostConstruct
    public void init() {
        try {
            // only the first 8 Bytes of the constructor argument are used as material for generating the keySpec
            keySpec = new DESKeySpec(secret.getBytes(CHARSET));
            keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
            key = keyFactory.generateSecret(keySpec);
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }

    public String encode(String text) {
        try {
            byte[] cleartext = text.getBytes(CHARSET);
            Cipher cipher = Cipher.getInstance(ALGORITHM); // cipher is not thread
            // safe
            cipher.init(Cipher.ENCRYPT_MODE, key);
            String encryptedPwd = new String(Base64.getEncoder().encode(cipher.doFinal(cleartext)));

            return encryptedPwd;
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return null;
        }

    }

    public String decode(String text) {
        try {
            byte[] encrypedPwdBytes = Base64.getDecoder().decode(text);

            Cipher cipher = Cipher.getInstance(ALGORITHM);// cipher is not thread safe
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] plainTextPwdBytes = (cipher.doFinal(encrypedPwdBytes));
            return new String(plainTextPwdBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return null;
        }
    }

}
