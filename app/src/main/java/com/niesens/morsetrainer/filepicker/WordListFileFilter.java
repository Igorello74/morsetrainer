/*
 *  Copyright (C) 2020 Claus Niesen
 *
 *  This file is part of Claus' Morse Trainer.
 *
 *  Claus' Morse Trainer is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Claus' Morse Trainer is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Claus' Morse Trainer.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.niesens.morsetrainer.filepicker;

import java.io.File;
import java.io.FileFilter;

public class WordListFileFilter implements FileFilter {

    @Override
    public boolean accept(File pathname) {
        return pathname.isFile() && !pathname.isHidden() && pathname.getName().toLowerCase().endsWith(".txt");
    }
}
