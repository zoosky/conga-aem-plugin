/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2018 wcm.io
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package io.wcm.devops.conga.plugins.aem.tooling.crypto.cli;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.function.Function;

import org.apache.commons.io.FileUtils;

import io.wcm.devops.conga.plugins.ansible.util.AnsibleVaultPassword;
import net.wedjaa.ansible.vault.crypto.VaultHandler;

/**
 * Encrypts and decrypts file with Ansible Vault.
 */
public final class AnsibleVault {

  private AnsibleVault() {
    // static methods only
  }

  /**
   * Encrypts file with Ansible vault.
   * @param file File to encrypt
   * @throws IOException I/O exception
   */
  public static void encrypt(File file) throws IOException {
    handleFile(file, data -> {
      try {
        return VaultHandler.encrypt(data, AnsibleVaultPassword.get());
      }
      catch (IOException ex) {
        throw new RuntimeException("Unable to encrypt file " + file.getPath(), ex);
      }
    });
  }

  /**
   * Decrypts file with Ansible vault.
   * @param file File to decrypt
   * @throws IOException I/O exception
   */
  public static void decrypt(File file) throws IOException {
    handleFile(file, data -> {
      try {
        return VaultHandler.decrypt(data, AnsibleVaultPassword.get());
      }
      catch (IOException ex) {
        throw new RuntimeException("Unable to decrypt file " + file.getPath(), ex);
      }
    });
  }

  private static void handleFile(File file, Function<byte[], byte[]> vaultHandler) throws IOException {
    if (!file.exists()) {
      throw new FileNotFoundException(file.getPath());
    }
    byte[] input = FileUtils.readFileToByteArray(file);
    byte[] output = vaultHandler.apply(input);
    file.delete();
    FileUtils.writeByteArrayToFile(file, output);
  }

}
