/*
 * @(#)SecureProvider.java     2011-10-26
 */
package org.dommons.security.cipher;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandomSpi;

/**
 * 安全密钥提供器 使用 SUN 的提供器
 * @author Demon 2011-10-26
 */
class SecureProvider extends Provider {

	private static final long serialVersionUID = -4367327573601987725L;

	public static final String algorithm = "SHA1PRNG";

	public static SecureProvider instance = new SecureProvider();

	/**
	 * 构造函数
	 */
	public SecureProvider() {
		super("Secure", 1.0, "Common Secure");
		put("SecureRandom.SHA1PRNG", "org.dommons.security.cipher.SecureProvider$SecureRandomHandler");
	}

	public final static class SecureRandomHandler extends SecureRandomSpi implements Serializable {

		private static final long serialVersionUID = -6780069785404039388L;

		private static void updateState(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2) {
			int i = 1;
			int j = 0;
			int k = 0;
			int l = 0;

			for (int i1 = 0; i1 < paramArrayOfByte1.length; ++i1) {
				j = paramArrayOfByte1[i1] + paramArrayOfByte2[i1] + i;

				k = (byte) j;

				l |= ((paramArrayOfByte1[i1] != k) ? 1 : 0);
				paramArrayOfByte1[i1] = (byte) k;

				i = j >> 8;
			}

			if (l == 0) {
				int tmp79_78 = 0;
				byte[] tmp79_77 = paramArrayOfByte1;
				tmp79_77[tmp79_78] = (byte) (tmp79_77[tmp79_78] + 1);
			}
		}

		private transient MessageDigest digest;
		private byte[] state;
		private byte[] remainder;

		private int remCount;

		public SecureRandomHandler() {
			init(null);
		}

		public byte[] engineGenerateSeed(int paramInt) {
			throw new UnsupportedOperationException();
		}

		public synchronized void engineNextBytes(byte[] paramArrayOfByte) {
			int i = 0;

			byte[] arrayOfByte1 = this.remainder;

			if (this.state == null) throw new UnsupportedOperationException();

			int k = this.remCount;
			int j;
			int l;
			if (k > 0) {
				j = (paramArrayOfByte.length - i < 20 - k) ? paramArrayOfByte.length - i : 20 - k;

				for (l = 0; l < j; ++l) {
					paramArrayOfByte[l] = arrayOfByte1[k];
					arrayOfByte1[(k++)] = 0;
				}
				this.remCount += j;
				i += j;
			}

			while (i < paramArrayOfByte.length) {
				this.digest.update(this.state);
				arrayOfByte1 = this.digest.digest();
				updateState(this.state, arrayOfByte1);

				j = (paramArrayOfByte.length - i > 20) ? 20 : paramArrayOfByte.length - i;

				for (l = 0; l < j; ++l) {
					paramArrayOfByte[(i++)] = arrayOfByte1[l];
					arrayOfByte1[l] = 0;
				}
				this.remCount += j;
			}

			this.remainder = arrayOfByte1;
			this.remCount %= 20;
		}

		public synchronized void engineSetSeed(byte[] paramArrayOfByte) {
			if (this.state != null) {
				this.digest.update(this.state);
				for (int i = 0; i < this.state.length; ++i)
					this.state[i] = 0;
			}
			this.state = this.digest.digest(paramArrayOfByte);
		}

		private void init(byte[] paramArrayOfByte) {
			try {
				this.digest = MessageDigest.getInstance("SHA");
			} catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
				throw new InternalError("internal error: SHA-1 not available.");
			}

			if (paramArrayOfByte != null) engineSetSeed(paramArrayOfByte);
		}

		private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
			paramObjectInputStream.defaultReadObject();
			try {
				this.digest = MessageDigest.getInstance("SHA");
			} catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
				throw new InternalError("internal error: SHA-1 not available.");
			}
		}
	}
}