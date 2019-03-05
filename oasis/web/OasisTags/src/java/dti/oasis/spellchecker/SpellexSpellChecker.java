package dti.oasis.spellchecker;

import com.spellex.ssce.PropSpellingSession;
import com.spellex.ssce.MemTextLexicon;
import com.spellex.ssce.Lexicon;
import com.spellex.ssce.LexiconUpdateException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.ServletContext;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

import dti.oasis.app.ApplicationContext;
import dti.oasis.struts.ActionHelper;
import dti.oasis.util.LogUtils;

/**
 * This is a utility class for Spellex Spell Checker Engine
 * <p>(C) 2009 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 18, 2009
 *
 * @author mgitelman
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */

public class SpellexSpellChecker {
    private SpellexSpellChecker(ServletContext context) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "SpellexSpellChecker", new Object[]{context});
        }
        init(context);
    }

    public static SpellexSpellChecker getInstance(ServletContext context) {
        Logger l = LogUtils.getLogger(SpellexSpellChecker.class);
        if (l.isLoggable(Level.FINER)) {
            l.entering(SpellexSpellChecker.class.getName(), "getInstance", new Object[]{context});
        }

        if(c_instance == null) {
            l.logp(Level.FINE, SpellexSpellChecker.class.getName(), "getInstance", "Creating New Instance");
            c_instance = new SpellexSpellChecker(context);
        } else {
            l.logp(Level.FINE, SpellexSpellChecker.class.getName(), "getInstance", "Returning Instance");
        }
        return c_instance;
    }

    //Get Spellex Properties from App Configuration
    private void init(ServletContext context) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "init", new Object[]{context});
        }

        initCount++;
        if (l.isLoggable(Level.FINE)) {
            l.logp(Level.FINE, getClass().getName(), "init", "initCount = " + initCount);
        }

        if (m_spellingProperties == null)
            m_spellingProperties = ApplicationContext.getInstance().getProperties();

//        java.util.Enumeration e = m_spellingProperties.propertyNames();
//
//        while (e.hasMoreElements()) {
//            String key = (String) e.nextElement();
//            l.logp(Level.FINE, getClass().getName(), "init", key + " -- " + m_spellingProperties.getProperty(key));
//        }

        //Unused
//        String mainLexPath = context.getRealPath(m_lexPath) + System.getProperty("file.separator");
//        String userLexPath = "";

        m_spellerTemplate =
            new PropSpellingSession(m_spellingProperties, null, "Spelling.", null,
                null);
        if (l.isLoggable(Level.FINE)) {
            l.logp(Level.FINE, getClass().getName(), "init", "m_spellerTemplate.getLexicons().length: " + m_spellerTemplate.getLexicons().length);

            for (int i = 0; i < m_spellerTemplate.getLexicons().length; i++) {
                l.logp(Level.FINE, getClass().getName(), "init", "m_spellerTemplate.getLexicons()[" + i + "]: " + m_spellerTemplate.getLexicons()[i]);
            }

        }
        
//        java.util.Enumeration e1 = m_spellerTemplate.getProperties().elements();
//
//        while (e1.hasMoreElements()) {
//            String key = (String) e1.nextElement();
//            l.logp(Level.FINE, getClass().getName(), "init", key + " -- " + m_spellerTemplate.getProperties().getProperty(key));
//        }
    }

    /**
     * Create a private PropSpellingSession object that can be used
     * to check spelling on behalf of this client session. The
     * PropSpellingSession object uses a private temporary lexicon
     * which is populated with words contained in a browser cookie
     * named "UserDict". The PropSpellingSession object also has
     * option settings initialized from a browser cookie named
     * "SpellingOptions". Default lexicons and options are copied
     * from the template saved in servletName + spellingSessionAttrName.
     * @param request Servlet request info
     * @return Private PropSpellingSession object, or null if the
     *  PropSpellingSession object could not be created
     */
    public PropSpellingSession getSpellingSession(HttpServletRequest request)  {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getSpellingSession", new Object[]{request});
        }
        ServletContext context = request.getSession().getServletContext();
        cloneCount++;
        if (l.isLoggable(Level.FINE)) {
            l.logp(Level.FINE, getClass().getName(), "getSpellingSession", "cloneCount = " + cloneCount);
        }

        // Create a clone of the PropSpellingSession template
        if (m_spellerTemplate == null) {
            l.logp(Level.FINE, getClass().getName(), "getSpellingSession", "Call initSpellingSession");
            init(context);
        } else {
            if (l.isLoggable(Level.FINE)) {
                for(int i=0; i<m_spellerTemplate.getLexicons().length; i++){
                    l.logp(Level.FINE, getClass().getName(), "getSpellingSession", "m_spellerTemplate.getLexicons()["+i+"]: "+m_spellerTemplate.getLexicons()[i]);
                }
            }

        }
        PropSpellingSession speller =
          (PropSpellingSession)m_spellerTemplate.clone();

        // The set of lexicons contained within the PropSpellingSession
        // template is shared among all instances of the
        // servlet, except for the temporary lexicon. The temp lexicon
        // contains words marked ignore-all and change-all by the user
        // and the client's user dictionary, which is saved in a cookie.
        // The temp lexicon is private to each servlet session.
        // Create a new temporary lexicon for this session.
        MemTextLexicon tempLex = new MemTextLexicon(Lexicon.ANY_LANG);
        speller.setTempLexicon(tempLex);

        // Load words from the user dictionary cookie into the
        // client's temporary lexicon.
        Cookie cookies[] = request.getCookies();
        for (int i = 0; cookies != null && i < cookies.length; ++i) {
            if (l.isLoggable(Level.FINE)) {
                l.logp(Level.FINE, getClass().getName(), "getSpellingSession", "cookies[i].getName() = " + cookies[i].getName());
            }
            if (cookies[i].getName().equals(userDictCookieName)) {
                String value = "";
                try {
                    value = java.net.URLDecoder.decode(cookies[i].getValue(), "utf-8");
                } catch (UnsupportedEncodingException e) {
                    value = java.net.URLDecoder.decode(cookies[i].getValue());
                }

                StringTokenizer st =
                  new StringTokenizer(value, ",");
                while (st.hasMoreTokens()) {
                    try {
                        tempLex.addWord(st.nextToken());
                    }
                    catch (LexiconUpdateException e) {
                        //Do nothing
                    }
                }
                break;
            }
        }

        // Set any client-settable spelling options. The options are
        // saved in a cookie.
        int options = 0;
        for (int i = 0; cookies != null && i < cookies.length; ++i) {
            if (cookies[i].getName().equals(optionsCookieName)) {
                try {
                    options = Integer.parseInt(cookies[i].getValue());
                }
                catch (Exception e) {
                    // Do nothing
                }
                break;
            }
        }

        if (options != 0) {
            int userOpts[] = {
                PropSpellingSession.CASE_SENSITIVE_OPT,
                PropSpellingSession.IGNORE_ALL_CAPS_WORD_OPT,
                PropSpellingSession.IGNORE_CAPPED_WORD_OPT,
                PropSpellingSession.IGNORE_MIXED_CASE_OPT,
                PropSpellingSession.IGNORE_MIXED_DIGITS_OPT,
                PropSpellingSession.REPORT_DOUBLED_WORD_OPT,
                PropSpellingSession.SUGGEST_SPLIT_WORDS_OPT,
                PropSpellingSession.IGNORE_DOMAIN_NAMES_OPT,
                PropSpellingSession.REPORT_UNCAPPED_OPT
            };
            for (int i = 0; i < userOpts.length; ++i) {
                speller.setOption(userOpts[i], (options & userOpts[i]) != 0);
            }
        }
        return speller;
    }

    private final static String optionsCookieName = "SpellingOptions";
	private final static String spellingSessionAttrName = "spellex.spellingSession";
	private final static String userDictCookieName = "UserDict";
//    private final static String m_lexPath = "/WEB-INF/classes/dti/lex";
    private Properties m_spellingProperties;
    private PropSpellingSession m_spellerTemplate;
    private static SpellexSpellChecker c_instance;
    private int initCount;
    private int cloneCount;
}
