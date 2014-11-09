/*
 * Copyright (C) 2011 0xlab - http://0xlab.org/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Authored by Wei-Ning Huang <azhuang@0xlab.org>
 */

package org.zeroxlab.owl;

import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Logger;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class Finder {
    private static final Logger LOG = Logger.getLogger(Finder.class.getName());
    public static MatchResult dispatch(IMatcher matcher, String haystack,
                                       String needle, double similarity)
        throws FileNotFoundException, TemplateNotFoundException {

        String pre = System.getProperty("user.dir");
        needle = (new File(pre, needle)).getAbsolutePath();

        IplImage img = cvLoadImage(haystack);

        if (img == null){
            throw new FileNotFoundException("can't open `" + haystack +"'");
        }
        IplImage tmpl = cvLoadImage(needle);
        if (tmpl == null){
            throw new FileNotFoundException("can't open `" + needle +"'");
        }
        MatchResult result = matcher.find(img, tmpl, similarity);
        LOG.info(String.format("MatchResut: %s", result));
        return result;
    }
};
