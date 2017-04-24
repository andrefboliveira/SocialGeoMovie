package com.socialgeomovie.utils;

import java.text.Normalizer;

import org.apache.commons.lang3.text.WordUtils;

public class IDParser {

	public static String createURI(String unformatted) {
		return Normalizer.normalize(WordUtils.capitalizeFully(unformatted), Normalizer.Form.NFD)
        .replaceAll("\\W", "");
		
	}
}
