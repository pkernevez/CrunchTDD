package com.octo.softshake.crunch.example;

import com.google.common.collect.ImmutableSet;
import org.apache.crunch.FilterFn;

import java.util.Set;


/**
 * A filter that removes known stop words.
 */
public class StopWordFilter extends FilterFn<String> {

    private static final Set<String> STOP_WORDS_ALL = ImmutableSet.copyOf(new String[]{"a", "and", "are", "as", "at",
            "be", "but", "by", "for", "if", "in", "into", "is", "it", "no", "not", "of", "on", "or", "s", "such",
            "t", "that", "the", "their", "then", "there", "these", "they", "this", "to", "was", "will", "with",
            "alors", "au", "aucuns", "à", "vos", "un", "une", "se", "de", "qu", "l", "d", "n", "se", "c", "cet", "cette",
            "aussi", "autre", "avant", "avec", "avoir", "bon", "car", "ce", "cela", "ces", "ceux", "chaque", "ci",
            "comme", "comment", "dans", "des", "du", "dedans", "dehors", "depuis", "devrait", "doit", "donc", "dos",
            "début", "elle", "elles", "en", "encore", "essai", "est", "et", "eu", "fait", "faites", "fois", "font",
            "hors", "ici", "il", "ils", "je 	juste", "la", "le", "les", "leur", "là", "ma", "maintenant", "mais",
            "mes", "mine", "moins", "mon", "mot", "même", "ni", "nommés", "notre", "nous", "ou", "où", "par", "parce",
            "pas", "peut", "peu", "plupart", "pour", "pourquoi", "quand", "que", "quel", "quelle", "quelles", "quels",
            "qui", "sa", "sans", "ses", "seulement", "si", "sien", "son", "sont", "sous", "soyez 	sujet", "sur",
            "ta", "tandis", "tellement", "tels", "tes", "ton", "tous", "tout", "trop", "très", "tu", "voient",
            "vont", "votre", "vous", "vu", "ça", "étaient", "état", "étions", "été", "être", "y", "ne", "Il", "au", "aux"});


    @Override
    public boolean accept(String word) {
        return !STOP_WORDS_ALL.contains(word);
    }

}
