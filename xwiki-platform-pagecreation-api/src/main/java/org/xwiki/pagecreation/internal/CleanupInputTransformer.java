/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.pagecreation.internal;

import java.text.Normalizer;

import org.xwiki.component.annotation.Component;
import org.xwiki.pagecreation.InputTransformer;

/**
 * Input transformer that "cleans up" the user input by removing diatrical for accentued letters, and by then removing
 * all non-word or non-whitespace characters from the input.
 * 
 * @version $Id$
 */
@Component("cleanup")
public class CleanupInputTransformer implements InputTransformer
{

    @Override
    public String transform(String input)
    {
        return Normalizer.normalize(input.trim(), java.text.Normalizer.Form.NFKD)
            .replaceAll("\\p{InCombiningDiacriticalMarks}+", "").replaceAll("[^\\w\\ ]", "");
    }

}
